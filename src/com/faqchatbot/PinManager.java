package com.faqchatbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PinManager — Feature 4: Pin Messages
 *
 * Holds all pinned messages in memory and notifies registered listeners
 * whenever the pin list changes (so PinnedPanel can refresh itself).
 */
public class PinManager {

    public static class PinnedMessage {
        public final String content;
        public final ChatBubble.BubbleType type;
        public final String pinnedAt;

        public PinnedMessage(String content, ChatBubble.BubbleType type) {
            this.content  = content;
            this.type     = type;
            this.pinnedAt = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        }
    }

    private static final List<PinnedMessage> pins = new ArrayList<>();
    private static final List<Runnable> listeners = new ArrayList<>();

    /** Pin a message. Duplicate content is ignored. */
    public static void pin(String content, ChatBubble.BubbleType type) {
        boolean exists = pins.stream().anyMatch(p -> p.content.equals(content));
        if (!exists) {
            pins.add(0, new PinnedMessage(content, type));
            notifyListeners();
        }
    }

    /** Unpin by content. */
    public static void unpin(String content) {
        pins.removeIf(p -> p.content.equals(content));
        notifyListeners();
    }

    public static List<PinnedMessage> getPins() {
        return Collections.unmodifiableList(pins);
    }

    public static int count() { return pins.size(); }

    /** Register a callback to be fired whenever pins change. */
    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (Runnable r : listeners) r.run();
    }
}
