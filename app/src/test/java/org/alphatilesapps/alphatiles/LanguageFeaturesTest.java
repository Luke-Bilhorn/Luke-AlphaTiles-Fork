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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The fixture pack's extra tiles: longer matches, syllable breaks, diacritics,
 * and the type lists games read.
 */
@RunWith(RobolectricTestRunner.class)
public class LanguageFeaturesTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_threeAndFourCharacterTilesWinOverShorterOnes() {
        assertEquals(Arrays.asList("sch"), textsOf("sch"));
        assertEquals(Arrays.asList("chsh"), textsOf("chsh"));
        assertEquals("sch", rebuilt("sch"));
        assertEquals("chsh", rebuilt("chsh"));
    }

    @Test
    public void test_instructionMarksAreNotTiles() {
        assertEquals(Arrays.asList("c", "a", "t"), textsOf("c#at"));
        assertEquals("cat", Start.wordList.stripInstructionCharacters("c#at"));
        assertEquals("kake", Start.wordList.stripInstructionCharacters("ka.ke"));
        assertEquals(Arrays.asList("ka", "ke"), syllableTexts("ka.ke"));
    }

    @Test
    public void test_spaceAndDashStaysBetweenConsonants() {
        assertEquals(Arrays.asList("c", "-", "t"), textsOf("c-t"));
        assertEquals("c-t", rebuilt("c-t"));
        assertTrue(Start.hasSAD);
    }

    @Test
    public void test_diacriticFollowsItsConsonant() {
        assertEquals(Arrays.asList("c", "'"), textsOf("c'"));
        assertEquals("c'", rebuilt("c'"));
    }

    @Test
    public void test_tilesLandInTheListsGamesRead() {
        assertTrue(texts(Start.CONSONANTS).contains("c"));
        assertTrue(texts(Start.CONSONANTS).contains("ng"));
        assertFalse(texts(Start.CONSONANTS).contains("x"));
        assertTrue(texts(Start.VOWELS).contains("a"));
        assertTrue(texts(Start.VOWELS).contains("เ◌"));
        assertTrue(texts(Start.Ds).contains("'"));
        assertTrue(texts(Start.ADs).contains("^"));
        assertTrue(texts(Start.TONES).contains("˥"));
        assertTrue(texts(Start.SAD).contains("-"));
        assertTrue(Start.MULTITYPE_TILES.contains("x"));
        assertEquals(Arrays.asList("ka", "ke", "ki"), Start.SYLLABLES);
    }

    @Test
    public void test_secondTypeKeepsItsOwnAudioName() {
        ArrayList<Start.Tile> tiles = Start.tileList.parseWordIntoTiles("cx", word("cx"));
        assertEquals("x_audio_b", tiles.get(1).getAudioNameAccountingForMultitypeSymbols());
    }

    @Test
    public void test_alphabetWrapsInBothDirections() {
        Start.Tile first = Start.tileList.get(0);
        Start.Tile last = Start.tileList.get(Start.tileList.size() - 1);
        assertEquals(first.text, Start.tileList.returnNextTile(last).text);
        assertEquals(last.text, Start.tileList.returnPreviousTile(first).text);
    }

    @Test
    public void test_distractorChoicesAreTheThreeAlternates() {
        Start.Tile t = tileByText("t");
        Set<String> choices = new HashSet<>();
        for (Start.Tile choice : Start.tileList.returnFourTileChoices(t, 2, "C")) {
            choices.add(choice.text);
        }
        assertEquals(new HashSet<>(Arrays.asList("t", "c", "a", "p")), choices);
    }

    @Test
    public void test_wordsContainingCAreCountedOnce() {
        Start.Tile c = tileByText("c");
        assertEquals(6, Start.wordList.numberOfWordsForActiveTile(c, 3));
        assertEquals(6, Start.wordList.wordsForActiveTile(c, 6, 3).length);
    }

    private static Start.Tile tileByText(String text) {
        for (Start.Tile tile : Start.tileList) {
            if (tile.text.equals(text) && tile.typeOfThisTileInstance.equals(tile.tileType)) {
                return tile;
            }
        }
        throw new AssertionError("fixture tiles have no " + text);
    }

    private static List<String> texts(List<Start.Tile> tiles) {
        ArrayList<String> names = new ArrayList<>();
        for (Start.Tile tile : tiles) {
            names.add(tile.text);
        }
        return names;
    }

    private static List<String> textsOf(String wordInLOP) {
        ArrayList<String> texts = new ArrayList<>();
        for (Start.Tile tile : Start.tileList.parseWordIntoTiles(wordInLOP, word(wordInLOP))) {
            texts.add(tile.text);
        }
        return texts;
    }

    private static List<String> syllableTexts(String wordInLOP) {
        ArrayList<String> texts = new ArrayList<>();
        for (Start.Syllable syllable : Start.syllableList.parseWordIntoSyllables(word(wordInLOP))) {
            texts.add(syllable.text);
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
}
