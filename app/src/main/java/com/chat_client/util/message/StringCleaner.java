package com.chat_client.util.message;

public class StringCleaner {
    public static String spaceTrim(String in) {
        StringBuilder spaceSplitBuffer = new StringBuilder(in.length());
        String[] splitIn = in.split(" ");
        for (String s : splitIn) {
            if (!s.isEmpty()) {
                spaceSplitBuffer.append(s).append(" ");
            }
        }
        spaceSplitBuffer.trimToSize();
        return spaceSplitBuffer.toString().trim();
    }

    public static String enterTrim(String in) {
        StringBuilder enterSplitBuffer = new StringBuilder(in.length());
        String[] splitIn = in.trim().split("\n");
        for (String s : splitIn) {
            if (!s.isEmpty()) {
                enterSplitBuffer.append(spaceTrim(s)).append("\n");
            }
        }
        enterSplitBuffer.trimToSize();
        return enterSplitBuffer.toString().trim();
    }

    public static String messageTrim(String in) {
        return enterTrim(in);
    }
}
