# 🤖 AI Assistant Pro 

> A feature-rich, multilingual AI chatbot desktop application built with **JavaFX** and powered by the **Groq API**. Designed for students, educators, and professionals who need a fast, intelligent assistant with a polished native UI.

---

## 📸 Overview

AI Assistant Pro is a fully-featured desktop chatbot that combines a local **FAQ engine** (TF-IDF based) with live **Groq LLM** responses — giving you instant answers for known topics and deep AI reasoning for everything else. It ships with 10 distinct features including voice input/output, multi-language support, session persistence, message pinning, and much more.

---

## ✨ Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | 🎙 **Voice Input** | Speak your questions — mic input transcribed in real time |
| 2 | 🔊 **Text-to-Speech** | AI responses read aloud with per-message speak/stop controls |
| 3 | 🔍 **Chat Search** | Full-text search across all sessions with highlighted snippets |
| 4 | 📌 **Pin Messages** | Pin any message from any chat; view all pins in one panel |
| 5 | 💾 **Session Persistence** | All chat sessions saved to disk and restored on next launch |
| 6 | 🔔 **Notification Sound** | Synthesised ping tone when AI finishes responding (no audio file needed) |
| 7 | 📊 **Word Count & Reading Time** | Every message shows word count and estimated reading time |
| 8 | 📤 **Export Chats** | Save conversations as `.txt` or styled `.html` files |
| 9 | 🌐 **Multi-Language UI** | Full UI in English, Arabic (RTL), Urdu (RTL), and French |
| 10 | ⌨️ **Keyboard Shortcuts** | 20+ shortcuts for power users with a dedicated shortcut reference panel |

### Additional Highlights

- **Dual AI Engine** — Local FAQ matching (TF-IDF + cosine similarity) for university queries; Groq LLM for open-ended questions
- **Multiple AI Models** — Switch between `llama-3.3-70b-versatile`, `llama-3.1-8b-instant`, `mixtral-8x7b-32768`, and `gemma2-9b-it`
- **AI Personas** — 10 built-in roles: General Assistant, Coding Expert, Science Teacher, Math Tutor, Creative Writer, Language Translator, Doctor Advisor, Legal Advisor, Career Coach, Fitness Trainer
- **Dark / Light Theme** — Toggle themes live from Settings
- **Markdown Rendering** — Bot responses rendered with full Markdown support (code blocks, tables, bold, italic, lists)
- **Animated Chat Bubbles** — Smooth fade-in and slide-up animations on every message
- **File Upload** — Attach `.txt`, `.md`, `.java`, `.py`, `.json`, or `.csv` files as context
- **Regenerate Response** — Re-ask the last prompt with one click or `Ctrl+R`
- **Adjustable Font Size** — Slider from 11 px to 20 px
- **Splash Screen** — Branded loading screen on launch

---

## 🏗️ Project Structure

```
src/
└── main/
    └── java/
        └── com/faqchatbot/
            ├── Main.java                    # JavaFX entry point & window bootstrap
            ├── ChatController.java          # Central UI controller (input, send, history)
            ├── ChatBubble.java              # Message bubble component (pin, copy, TTS, stats)
            ├── ChatSession.java             # Session data model (messages, metadata, JSON I/O)
            ├── ChatSearchPanel.java         # Feature 3 — cross-session search modal
            ├── FAQData.java                 # Hardcoded FAQ dataset (university Q&A)
            ├── FAQEngine.java               # TF-IDF + cosine similarity search engine
            ├── GroqClient.java              # Groq REST API client (multi-model, multi-persona)
            ├── SessionManager.java          # Disk-based session load / save / delete
            ├── Sidebar.java                 # Session list sidebar with delete support
            ├── SettingsPanel.java           # Settings modal (API key, model, persona, theme…)
            ├── PinManager.java              # Feature 4 — in-memory pin store with listeners
            ├── PinnedPanel.java             # Feature 4 — pinned messages modal
            ├── FileHandler.java             # TXT/HTML export + file upload reader
            ├── MarkdownRenderer.java        # Flexmark-based Markdown → HTML renderer
            ├── NotificationSoundManager.java# Feature 6 — PCM sine-wave ping generator
            ├── I18nManager.java             # Feature 9 — i18n string table (en/ar/ur/fr)
            ├── KeyboardShortcutsPanel.java  # Feature 10 — shortcuts reference modal
            ├── SpeechManager.java           # TTS engine wrapper
            └── VoiceInputManager.java       # Feature 1 — microphone input manager
resources/
    ├── style.css                            # Full dark/light theme stylesheet
    └── assets/
        └── icon.png                         # App icon
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| UI Framework | JavaFX 21 |
| AI API | [Groq Cloud](https://console.groq.com/) |
| HTTP Client | Java `java.net.http.HttpClient` |
| JSON | Google Gson + org.json |
| Markdown | [Flexmark-Java](https://github.com/vsch/flexmark-java) |
| Audio | `javax.sound.sampled` (PCM synthesis, no external files) |
| Persistence | JSON files in `~/AIAssistantPro/sessions/` |
| Build Tool | Maven (recommended) |

---

## 🚀 Getting Started

### Prerequisites

- Java **17** or higher
- Maven **3.8+**
- A free [Groq API key](https://console.groq.com/)

### Clone & Build

```bash
git clone https://github.com/YOUR_USERNAME/ai-assistant-pro.git
cd ai-assistant-pro
mvn clean package
```

### Run

```bash
mvn javafx:run
```

Or run the packaged JAR:

```bash
java -jar target/ai-assistant-pro-1.0.jar
```

### Set Your API Key

1. Launch the app
2. Click **⚙️ Settings** in the header (or press `Ctrl+,`)
3. Paste your Groq API key in the **API Configuration** field
4. Click **✓ Save & Close**

Your key is stored in the settings panel for the current session. To persist it across launches, add it to `GroqClient.java`:

```java
private static String API_KEY = "gsk_your_key_here";
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Enter` | Send message |
| `Shift + Enter` | New line in input |
| `Ctrl + R` | Regenerate last response |
| `Ctrl + L` | Clear chat |
| `Ctrl + N` | New chat session |
| `Ctrl + F` | Search all chats |
| `Ctrl + P` | Open pinned messages |
| `Ctrl + ,` | Open settings |
| `Ctrl + ?` | Show keyboard shortcuts |
| `Ctrl + E` | Export chat |
| `Ctrl + M` | Start / stop mic recording |
| `Ctrl + S` | Toggle notification sound |
| `Ctrl + 1–4` | Switch language (EN / AR / UR / FR) |
| `Escape` | Close any open dialog |

---

## 🌐 Multi-Language Support

The UI is fully translated into four languages. Switch instantly from **Settings → Language** or via `Ctrl+1` through `Ctrl+4`.

| Code | Language | Direction |
|------|----------|-----------|
| `en` | English | LTR |
| `ar` | العربية (Arabic) | **RTL** |
| `ur` | اردو (Urdu) | **RTL** |
| `fr` | Français (French) | LTR |

RTL layouts are automatically applied to input fields, result lists, and message snippets.

---

## 🧠 FAQ Engine

The built-in FAQ engine answers common **university-related questions** without an internet connection, including topics like:

- Admission requirements & deadlines
- Tuition fees & scholarships
- Exam schedules & attendance rules
- Hostel, library, transport, and campus facilities
- Transcripts, GPA calculation, and results

It uses a **TF-IDF vectoriser** with **cosine similarity** scoring. If the best match scores below the confidence threshold (`0.1`), the query is automatically escalated to the Groq LLM.

To add your own FAQs, edit `FAQData.java`:

```java
faqs.put("Your question here?", "Your answer here.");
```

---

## 📤 Export Formats

| Format | What you get |
|--------|-------------|
| **TXT** | Plain-text transcript with timestamps and a decorative header |
| **HTML** | Styled dark-theme page with coloured user/bot bubbles, ready to open in any browser |

---

## 🔧 Configuration Reference

All runtime settings are accessible via the **Settings panel** (`Ctrl+,`):

| Setting | Options |
|---------|---------|
| API Key | Groq key (`gsk_…`) |
| AI Model | llama-3.3-70b · llama-3.1-8b · mixtral-8x7b · gemma2-9b |
| AI Persona | 10 built-in roles |
| Theme | 🌙 Dark / ☀️ Light |
| Text-to-Speech | On / Off |
| Notification Sound | On / Off + live test |
| Font Size | 11 px – 20 px slider |
| Language | EN / AR / UR / FR |

---

## 📋 Maven Dependencies (pom.xml snippet)

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-web</artifactId>
        <version>21</version>
    </dependency>

    <!-- Groq / JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20231013</version>
    </dependency>

    <!-- Markdown -->
    <dependency>
        <groupId>com.vladsch.flexmark</groupId>
        <artifactId>flexmark-all</artifactId>
        <version>0.64.8</version>
    </dependency>
</dependencies>
```

---

## 🤝 Contributing

Contributions are welcome! Here's how:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add: your feature description"`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

Please keep code style consistent with existing files (JavaFX conventions, descriptive comments, feature-tagged sections).

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Your Name**  
📧 your.email@example.com  
🔗 [GitHub](https://github.com/YOUR_USERNAME) · [LinkedIn](https://linkedin.com/in/YOUR_USERNAME)

---

## 🙏 Acknowledgements

- [Groq](https://groq.com/) — blazing-fast LLM inference API
- [Flexmark-Java](https://github.com/vsch/flexmark-java) — Markdown parsing & rendering
- [JavaFX](https://openjfx.io/) — modern Java UI toolkit
- University FAQ dataset compiled for academic use

---

<div align="center">

**⭐ If you find this project useful, please give it a star!**

Made with ☕ and Java

</div>
