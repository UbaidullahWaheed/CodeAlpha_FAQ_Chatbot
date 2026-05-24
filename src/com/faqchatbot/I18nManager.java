package com.faqchatbot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * I18nManager — Feature 9: Multi-language UI
 *
 * Supports English, Arabic (RTL), Urdu (RTL), and French.
 * All UI strings are looked up via I18nManager.t("key").
 * Language preference is persisted across sessions.
 */
public class I18nManager {

    public enum Language {
        ENGLISH("English", "en", false),
        ARABIC("العربية", "ar", true),
        URDU("اردو", "ur", true),
        FRENCH("Français", "fr", false);

        public final String displayName;
        public final String code;
        public final boolean rtl;

        Language(String displayName, String code, boolean rtl) {
            this.displayName = displayName;
            this.code        = code;
            this.rtl         = rtl;
        }
    }

    private static Language current = Language.ENGLISH;
    private static final Preferences prefs =
            Preferences.userNodeForPackage(I18nManager.class);

    // ── String tables ─────────────────────────────────────────────────────────
    // key → [en, ar, ur, fr]
    private static final Map<String, String[]> STRINGS = new LinkedHashMap<>();

    static {
        // Header
        add("app.title",         "AI Assistant Pro",          "مساعد الذكاء الاصطناعي",     "اے آئی اسسٹنٹ پرو",       "Assistant IA Pro");
        add("app.subtitle",      "Pro · International",       "برو · دولي",                   "پرو · بین الاقوامی",       "Pro · International");
        add("header.online",     "Online",                    "متصل",                         "آن لائن",                  "En ligne");
        add("header.thinking",   "Thinking…",                 "يفكر…",                        "سوچ رہا ہے…",              "Réflexion…");
        add("header.regen",      "Regenerating…",             "يعيد الإنشاء…",                "دوبارہ بنا رہا ہے…",       "Régénération…");

        // Sidebar
        add("sidebar.newchat",   "+ New Chat",                "+ محادثة جديدة",               "+ نئی گفتگو",              "+ Nouveau Chat");
        add("sidebar.recent",    "RECENT CHATS",              "المحادثات الأخيرة",            "حالیہ گفتگو",              "CHATS RÉCENTS");

        // Input area
        add("input.placeholder", "Ask me anything…",          "اسألني أي شيء…",               "مجھ سے کچھ بھی پوچھیں…",  "Posez-moi une question…");
        add("input.hint",        "Shift+Enter = new line",    "Shift+Enter = سطر جديدة",      "Shift+Enter = نئی لائن",   "Shift+Entrée = nouvelle ligne");
        add("input.send",        "Send ➤",                   "إرسال ➤",                      "بھیجیں ➤",                "Envoyer ➤");
        add("input.charcount",   "/ 2000",                    "/ ٢٠٠٠",                       "/ ۲۰۰۰",                   "/ 2000");

        // Buttons / tooltips
        add("btn.copy",          "Copy to clipboard",         "نسخ إلى الحافظة",              "کلپ بورڈ پر کاپی کریں",   "Copier");
        add("btn.pin",           "Pin message",               "تثبيت الرسالة",                "پیغام پن کریں",            "Épingler");
        add("btn.unpin",         "Unpin message",             "إلغاء التثبيت",                "پن ہٹائیں",               "Désépingler");
        add("btn.speak",         "Read aloud",                "قراءة بصوت عالٍ",              "اونچا پڑھیں",              "Lire à voix haute");
        add("btn.stop",          "Stop reading",              "إيقاف القراءة",                "پڑھنا بند کریں",          "Arrêter");
        add("btn.settings",      "Settings",                  "الإعدادات",                    "ترتیبات",                  "Paramètres");
        add("btn.export",        "Export Chat",               "تصدير المحادثة",               "گفتگو برآمد کریں",        "Exporter");
        add("btn.clear",         "Clear Chat",                "مسح المحادثة",                 "گفتگو صاف کریں",          "Effacer");
        add("btn.regen",         "🔄 Regenerate",             "🔄 إعادة الإنشاء",             "🔄 دوبارہ بنائیں",         "🔄 Régénérer");
        add("btn.upload",        "Upload File",               "رفع ملف",                      "فائل اپلوڈ کریں",         "Télécharger");
        add("btn.pinned",        "📍 Pinned",                 "📍 المثبتة",                   "📍 پن کیے گئے",           "📍 Épinglés");
        add("btn.mic",           "🎙 Speak",                  "🎙 تحدث",                       "🎙 بولیں",                 "🎙 Parler");
        add("btn.mic.stop",      "⏹ Stop",                   "⏹ إيقاف",                      "⏹ بند کریں",              "⏹ Arrêter");
        add("btn.search",        "🔍 Search",                 "🔍 بحث",                       "🔍 تلاش",                  "🔍 Recherche");

        // Settings panel
        add("settings.title",    "⚙️  Settings",             "⚙️  الإعدادات",               "⚙️  ترتیبات",              "⚙️  Paramètres");
        add("settings.lang",     "🌐 Language",               "🌐 اللغة",                     "🌐 زبان",                  "🌐 Langue");
        add("settings.notif",    "🔔 Notification Sound",     "🔔 صوت الإشعار",              "🔔 اطلاع کی آواز",        "🔔 Son de notification");
        add("settings.save",     "✓  Save & Close",           "✓  حفظ وإغلاق",               "✓  محفوظ کریں",           "✓  Sauvegarder");

        // Typing indicator
        add("typing",            "🤖  AI is thinking…",       "🤖  الذكاء الاصطناعي يفكر…", "🤖  اے آئی سوچ رہا ہے…",  "🤖  L'IA réfléchit…");
        add("typing.regen",      "🤖  Regenerating…",         "🤖  يعيد الإنشاء…",           "🤖  دوبارہ بنا رہا ہے…",   "🤖  Régénération…");

        // Pinned panel
        add("pinned.title",      "📍 Pinned Messages",        "📍 الرسائل المثبتة",           "📍 پن کیے گئے پیغامات",   "📍 Messages épinglés");
        add("pinned.empty",      "No pinned messages yet.\nClick 📌 on any message.",
                                  "لا توجد رسائل مثبتة.\nانقر على 📌 في أي رسالة.",
                                  "ابھی کوئی پن نہیں۔\n📌 پر کلک کریں۔",
                                  "Aucun message épinglé.\nCliquez sur 📌.");
        add("pinned.user",       "👤 You",                   "👤 أنت",                       "👤 آپ",                    "👤 Vous");
        add("pinned.ai",         "🤖 AI",                    "🤖 الذكاء الاصطناعي",         "🤖 اے آئی",               "🤖 IA");

        // Search panel
        add("search.title",      "🔍 Search Chats",           "🔍 بحث في المحادثات",         "🔍 گفتگو تلاش کریں",      "🔍 Rechercher");
        add("search.placeholder","Search messages…",          "ابحث في الرسائل…",            "پیغامات تلاش کریں…",      "Rechercher des messages…");
        add("search.noresult",   "No results found.",         "لم يتم العثور على نتائج.",    "کوئی نتیجہ نہیں ملا۔",    "Aucun résultat trouvé.");
    }

    private static void add(String key, String en, String ar, String ur, String fr) {
        STRINGS.put(key, new String[]{en, ar, ur, fr});
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public static void initialize() {
        String saved = prefs.get("language", "ENGLISH");
        try { current = Language.valueOf(saved); }
        catch (Exception e) { current = Language.ENGLISH; }
    }

    public static void setLanguage(Language lang) {
        current = lang;
        prefs.put("language", lang.name());
    }

    public static Language getLanguage() { return current; }

    public static boolean isRTL() { return current.rtl; }

    /** Translate a key to the current language. Returns the key itself if not found. */
    public static String t(String key) {
        String[] vals = STRINGS.get(key);
        if (vals == null) return key;
        int idx = current.ordinal(); // en=0, ar=1, ur=2, fr=3
        return (idx < vals.length) ? vals[idx] : vals[0];
    }

    public static Language[] getAllLanguages() { return Language.values(); }
}
