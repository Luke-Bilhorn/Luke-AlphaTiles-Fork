package org.alphatilesapps.alphatiles;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UtilTest {

    @Test
    public void test_returnPlayerStringToAppend() {

        // This is probably invalid. Should expect "-01" or throw?
        assertEquals("0-1", Util.returnPlayerStringToAppend(-1));

        assertEquals("00", Util.returnPlayerStringToAppend(0));
        assertEquals("09", Util.returnPlayerStringToAppend(9));
        assertEquals("10", Util.returnPlayerStringToAppend(10));
    }

    @Test
    public void test_getMinFontSizeStaysFinite() {
        float shortWord = Util.getMinFontSize(new String[]{"a"});
        float longWord = Util.getMinFontSize(new String[]{"a", "alphabet"});
        assertTrue(shortWord > 0);
        assertTrue(longWord > 0);
        assertTrue(longWord <= shortWord);
        Util.logMemory();
    }
}