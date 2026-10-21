package org.alphatilesapps.alphatiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads the committed fixture language flavor and checks the behavior later
 * efficiency work is most likely to change: how a word becomes tiles, how those
 * tiles become the word again, and which stage the word is introduced in.
 *
 * Run with: bash ./gradlew :app:testFixtureDebugUnitTest
 */
@RunWith(RobolectricTestRunner.class)
public class LanguagePackTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_packReportsItsScriptAndPlaceholder() {
        assertEquals("Thai", Start.scriptType);
        assertEquals("◌", Start.placeholderCharacter);
        assertEquals("Fixture", Start.langInfoList.find("Game Name"));
        assertEquals("LTR", Start.langInfoList.find("Script direction (LTR or RTL)"));
        assertTrue(Start.differentiatesTileTypes);
        assertEquals(1.0, Start.stageCorrespondenceRatio, 0.0);
    }

    @Test
    public void test_longestTileMatchWins() {
        assertEquals(Arrays.asList("sh", "i", "p"), textsOf("ship"));
    }

    @Test
    public void test_leadingVowelRoundTrip() {
        assertEquals(Arrays.asList("เ◌", "ก"), textsOf("เก"));
        assertEquals("เก", rebuilt("เก"));
    }

    @Test
    public void test_followingVowelRoundTrip() {
        assertEquals(Arrays.asList("ก", "◌า"), textsOf("กา"));
        assertEquals("กา", rebuilt("กา"));
    }

    @Test
    public void test_plainWordRoundTrip() {
        assertEquals(Arrays.asList("c", "a", "t"), textsOf("cat"));
        assertEquals("cat", rebuilt("cat"));
        assertEquals("ship", rebuilt("ship"));
    }

    @Test
    public void test_multitypeTileUsesTheWordListType() {
        // "cx" is a consonant plus the second type of "x". A word that is only
        // that second type is dropped by the Thai reorder, so it cannot show this.
        ArrayList<Start.Tile> tiles = Start.tileList.parseWordIntoTiles("cx", word("cx"));
        assertEquals(Arrays.asList("c", "x"), textsOf("cx"));
        assertEquals("C", tiles.get(0).typeOfThisTileInstance);
        assertEquals("X", tiles.get(1).typeOfThisTileInstance);
        assertEquals("x_audio_b", tiles.get(1).audioForThisTileType);
        assertTrue(Start.MULTITYPE_TILES.contains("x"));
        assertEquals("cx", rebuilt("cx"));
    }

    @Test
    public void test_wordsEnterTheStageThatCoversEveryTile() {
        assertEquals(Arrays.asList("cat", "เก", "กา"), stageNames(1));
        assertEquals(Arrays.asList("ship", "cx"), stageNames(2));
    }

    @Test
    public void test_gamesAndKeyboardLoad() {
        assertEquals(1, Start.gameList.size());
        assertEquals("Brazil", Start.gameList.get(0).country);
        assertEquals("T", Start.gameList.get(0).mode);
        assertFalse(Start.hasSyllableGames);
        assertEquals(1, Start.keyList.size());
        assertEquals("c", Start.keyList.get(0).text);
    }

    private static List<String> textsOf(String wordInLOP) {
        ArrayList<String> texts = new ArrayList<>();
        for (Start.Tile tile : Start.tileList.parseWordIntoTiles(wordInLOP, word(wordInLOP))) {
            texts.add(tile.text);
        }
        return texts;
    }

    private static String rebuilt(String wordInLOP) {
        Start.Word word = word(wordInLOP);
        return GameActivity.combineTilesToMakeWord(
                Start.tileList.parseWordIntoTiles(word.wordInLOP, word), word, -1);
    }

    private static Start.Word word(String wordInLOP) {
        Start.Word word = Start.lopWordHashMap.find(wordInLOP);
        if (word == null) {
            throw new AssertionError("fixture word list has no " + wordInLOP);
        }
        return word;
    }

    private static List<String> stageNames(int stageNumber) {
        ArrayList<String> names = new ArrayList<>();
        for (Start.Word word : Start.wordStagesLists.get(stageNumber - 1)) {
            names.add(word.wordInLOP);
        }
        return names;
    }
}
