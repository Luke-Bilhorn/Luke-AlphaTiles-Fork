package org.alphatilesapps.alphatiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.HashSet;

/** Chile's word filter and keyboard, which run before the guessing screen opens. */
@RunWith(RobolectricTestRunner.class)
public class ChilePackTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_onlyWordsInTheLengthWindowBecomeGuesses() {
        Chile.ChileData data = Chile.chilePreProcess();

        assertEquals(7, data.keyboardWidth);
        assertEquals(4, data.words.size());
        assertTrue(data.fontScale > 0);
        assertFalse(Float.isNaN(data.fontScale));
        assertEquals("c", data.keys[0]);
        assertEquals(new HashSet<>(Arrays.asList("c", "a", "t", "sh", "i", "p", "-")),
                new HashSet<>(Arrays.asList(data.keys)));
    }

    @Test
    public void test_openingChileBuildsTheGuessList() {
        assertNull(Chile.data);
        FixturePack.open(Chile.class, FixturePack.gameIntent(1, 1, 1, "T", 1, "Chile"));
        assertNotNull(Chile.data);
        assertEquals(4, Chile.data.words.size());
    }
}
