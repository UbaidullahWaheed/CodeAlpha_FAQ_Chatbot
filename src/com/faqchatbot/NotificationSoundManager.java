package com.faqchatbot;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

/**
 * NotificationSoundManager — Feature 6: Notification Sound
 *
 * Plays a short synthesised "ping" tone when the AI finishes responding.
 * No external audio file required — the tone is generated on the fly using
 * a sine wave so the app stays self-contained.
 *
 * Usage:  NotificationSoundManager.ping();
 */
public class NotificationSoundManager {

    private static boolean enabled = true;

    /** Play the ping tone in a background thread (non-blocking). */
    public static void ping() {
        if (!enabled) return;
        Thread t = new Thread(() -> {
            try {
                byte[] wav = generateSineWav(880f, 180); // A5, 180 ms
                AudioInputStream ais = new AudioInputStream(
                        new ByteArrayInputStream(wav),
                        pcmFormat(),
                        wav.length / pcmFormat().getFrameSize());
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                // Slightly reduce volume
                FloatControl vol = (FloatControl) clip.getControl(
                        FloatControl.Type.MASTER_GAIN);
                vol.setValue(Math.max(vol.getMinimum(), vol.getValue() - 6f));
                clip.start();
                Thread.sleep(300); // wait for it to finish
                clip.close();
            } catch (Exception e) {
                // Silently ignore if audio is unavailable
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Sine-wave generator ───────────────────────────────────────────────────
    private static byte[] generateSineWav(float frequency, int durationMs) {
        AudioFormat fmt = pcmFormat();
        int samples = (int) (fmt.getSampleRate() * durationMs / 1000.0);
        byte[] buf  = new byte[samples * 2]; // 16-bit = 2 bytes/sample
        for (int i = 0; i < samples; i++) {
            double angle  = 2.0 * Math.PI * i * frequency / fmt.getSampleRate();
            // Fade-in/fade-out envelope to avoid clicks
            double env    = envelope(i, samples);
            short  sample = (short) (Short.MAX_VALUE * 0.5 * env * Math.sin(angle));
            buf[i * 2]     = (byte) (sample & 0xFF);
            buf[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return buf;
    }

    /** Linear ramp — 10 % fade-in, 10 % fade-out */
    private static double envelope(int i, int total) {
        int ramp = total / 10;
        if (i < ramp)         return (double) i / ramp;
        if (i > total - ramp) return (double) (total - i) / ramp;
        return 1.0;
    }

    private static AudioFormat pcmFormat() {
        return new AudioFormat(44100f, 16, 1, true, false);
    }

    // ── Enabled flag ──────────────────────────────────────────────────────────
    public static void setEnabled(boolean val) { enabled = val; }
    public static boolean isEnabled()           { return enabled; }
}
