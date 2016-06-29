package com.chat_client.util;

public class StringCleaner {
    public static String spaceTrim(String in) {
        StringBuffer spaceSplitBuffer = new StringBuffer(in.length());
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
        StringBuffer enterSplitBuffer = new StringBuffer(in.length());
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
