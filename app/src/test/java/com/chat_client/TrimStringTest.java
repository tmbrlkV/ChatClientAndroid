package com.chat_client;

import com.chat_client.util.message.StringCleaner;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrimStringTest {
    @Test
    public void withoutSpaces() throws Exception {
        assertEquals("a b c", StringCleaner.spaceTrim(" a b   c   "));
    }

    @Test
    public void withoutEnters() throws Exception {
        assertEquals("a\nb\nc", StringCleaner.enterTrim("\n\na\n\n\nb\nc\n\n\n"));
    }
    @Test
    public void messageTrimTest() throws Exception {
        assertEquals("a\nb c\ncd", StringCleaner.messageTrim("\n\n\na\n\n\n  b     " +
                "c\n\n\ncd      \n\n"));
    }
}