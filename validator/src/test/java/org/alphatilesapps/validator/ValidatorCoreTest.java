package org.alphatilesapps.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

/** The validator checks that do not talk to Google. */
public class ValidatorCoreTest {

    @Test
    public void test_wordDistanceCountsEdits() {
        assertEquals(0, FilePresence.costOfSubstitution('a', 'a'));
        assertEquals(1, FilePresence.costOfSubstitution('a', 'b'));
        assertEquals(1, FilePresence.min(3, 1, 2));
        assertEquals(0, FilePresence.wordDistance("cat", "cat"));
        assertEquals(1, FilePresence.wordDistance("cat", "car"));
        assertEquals(3, FilePresence.wordDistance("", "cat"));
    }

    @Test
    public void test_messagesMatchOnTagAndText() {
        Message first = new Message(Message.Tag.FilePresence, "missing clip");
        Message second = new Message(Message.Tag.FilePresence, "missing clip");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(new Message(Message.Tag.Etc, "missing clip")));
        assertEquals(3, Message.allTags().size());
    }

    @Test
    public void test_emptyPackReportsZeroAtEveryStage() {
        String report = StagesChecks.check(
                new ArrayList<>(), new Validator.TileList(), 1.0, false);
        assertTrue(report.contains("Stage 1: 0 words"));
        assertTrue(report.contains("0 tiles"));
        assertTrue(report.contains("Stage 7:"));
    }
}
