package com.faqchatbot;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.function.Consumer;

/**
 * VoiceInputManager — Feature 1: Voice Input
 *
 * Records microphone audio (WAV) and sends it to Groq's Whisper API
 * for transcription.  The result is delivered via a callback so the
 * caller can paste it into the input field on the JavaFX thread.
 *
 * Usage:
 *   VoiceInputManager.startRecording();
 *   …user speaks…
 *   VoiceInputManager.stopRecording(transcribed -> Platform.runLater(() -> field.setText(transcribed)));
 */
public class VoiceInputManager {

    private static final String WHISPER_URL =
            "https://api.groq.com/openai/v1/audio/transcriptions";

    private static TargetDataLine micLine;
    private static Thread recordThread;
    private static ByteArrayOutputStream audioBuffer;
    private static volatile boolean recording = false;
    private static boolean available = false;

    /** Call once at startup to check mic availability. */
    public static void initialize() {
        try {
            AudioFormat fmt = audioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
            available = AudioSystem.isLineSupported(info);
        } catch (Exception e) {
            available = false;
        }
    }

    public static boolean isAvailable() { return available; }
    public static boolean isRecording()  { return recording; }

    /** Begin capturing audio from the default microphone. */
    public static boolean startRecording() {
        if (!available || recording) return false;
        try {
            AudioFormat fmt = audioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
            micLine = (TargetDataLine) AudioSystem.getLine(info);
            micLine.open(fmt);
            micLine.start();
            audioBuffer = new ByteArrayOutputStream();
            recording = true;

            recordThread = new Thread(() -> {
                byte[] buf = new byte[4096];
                while (recording) {
                    int n = micLine.read(buf, 0, buf.length);
                    if (n > 0) audioBuffer.write(buf, 0, n);
                }
            });
            recordThread.setDaemon(true);
            recordThread.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            available = false;
            return false;
        }
    }

    /**
     * Stop recording and transcribe asynchronously.
     * @param callback receives the transcribed text (or an error string starting with "⚠️")
     */
    public static void stopRecording(Consumer<String> callback) {
        if (!recording) return;
        recording = false;
        if (micLine != null) { micLine.stop(); micLine.close(); }

        byte[] rawPcm = audioBuffer.toByteArray();
        Thread worker = new Thread(() -> {
            try {
                // Write WAV to a temp file
                File wavFile = File.createTempFile("voice_input_", ".wav");
                wavFile.deleteOnExit();
                writeWav(rawPcm, wavFile);

                // Send to Groq Whisper
                String result = transcribe(wavFile);
                callback.accept(result);
            } catch (Exception e) {
                callback.accept("⚠️ Transcription error: " + e.getMessage());
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    // ── Whisper API call ──────────────────────────────────────────────────────
    private static String transcribe(File wavFile) throws Exception {
        String boundary = "----VoiceBoundary" + System.currentTimeMillis();
        byte[] fileBytes = Files.readAllBytes(wavFile.toPath());

        // Build multipart body manually (no external lib needed)
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(body);

        ps.print("--" + boundary + "\r\n");
        ps.print("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"\r\n");
        ps.print("Content-Type: audio/wav\r\n\r\n");
        ps.flush();
        body.write(fileBytes);
        ps.print("\r\n--" + boundary + "\r\n");
        ps.print("Content-Disposition: form-data; name=\"model\"\r\n\r\n");
        ps.print("whisper-large-v3\r\n");
        ps.print("--" + boundary + "\r\n");
        ps.print("Content-Disposition: form-data; name=\"response_format\"\r\n\r\n");
        ps.print("json\r\n");
        ps.print("--" + boundary + "--\r\n");
        ps.flush();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WHISPER_URL))
                .header("Authorization", "Bearer " + GroqClient.getApiKey())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpResponse<String> resp = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Parse {"text": "..."} from response
        String respBody = resp.body();
        if (respBody.contains("\"text\"")) {
            int start = respBody.indexOf("\"text\"") + 8; // skip "text":"
            int end   = respBody.indexOf("\"", start);
            return respBody.substring(start, end).trim();
        }
        if (respBody.contains("\"error\"")) {
            return "⚠️ Whisper API error: " + respBody;
        }
        return "⚠️ Unexpected response: " + respBody;
    }

    // ── Audio helpers ─────────────────────────────────────────────────────────
    private static AudioFormat audioFormat() {
        return new AudioFormat(16000f, 16, 1, true, false);
    }

    private static void writeWav(byte[] pcm, File out) throws Exception {
        AudioFormat fmt = audioFormat();
        AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(pcm),
                fmt,
                pcm.length / fmt.getFrameSize());
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
    }
}
