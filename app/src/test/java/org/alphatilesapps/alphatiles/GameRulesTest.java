package org.alphatilesapps.alphatiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules for the country games that do not need a recorded clip: which word is
 * dealt, when a loteria is won, how pages are split, and the points header.
 */
@RunWith(RobolectricTestRunner.class)
public class GameRulesTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_brazilDealsAStageOneWord() {
        Brazil brazil = open("Brazil", 1, 1, 1, "T");
        List<String> stageOne = stageNames(1);
        for (int i = 0; i < 8; i++) {
            assertTrue(stageOne.contains(brazil.refWord.wordInLOP));
            brazil.repeatLocked = false;
            brazil.playAgain();
        }
        assertEquals("1", ((TextView) brazil.findViewById(R.id.challengeLevelView)).getText().toString());
    }

    @Test
    public void test_brazilHeaderShowsTheConsonantLevelAsOne() {
        Brazil brazil = open("Brazil", 1, 4, 1, "T");
        assertEquals("1", ((TextView) brazil.findViewById(R.id.challengeLevelView)).getText().toString());
    }

    @Test
    public void test_georgiaHeaderDropsTheSyllableOffset() {
        Georgia georgia = open("Georgia", 5, 7, 1, "T");
        assertEquals("1", ((TextView) georgia.findViewById(R.id.challengeLevelView)).getText().toString());
    }

    @Test
    public void test_thailandHeaderUsesTheHundredsDigit() {
        Thailand thailand = open("Thailand", 6, 111, 1, "T");
        assertEquals("1", ((TextView) thailand.findViewById(R.id.challengeLevelView)).getText().toString());
    }

    @Test
    public void test_sudanPutsTilesAndSyllablesOnOnePage() {
        Sudan tiles = open("Sudan", 2, 1, 1, "T");
        assertEquals(0, tiles.numPages);
        assertFalse(tiles.tilePagesLists.get(0).isEmpty());
        TextView firstTile = tiles.findViewById(R.id.tile01);
        assertEquals(tiles.tilePagesLists.get(0).get(0).text, firstTile.getText().toString());

        Sudan syllables = open("Sudan", 2, 1, 1, "S");
        assertEquals(0, syllables.numPages);
        assertEquals(3, syllables.syllablePagesLists.get(0).size());
        TextView firstSyllable = syllables.findViewById(R.id.tile01);
        assertEquals("ka", firstSyllable.getText().toString());
    }

    @Test
    public void test_malaysiaShowsTheStageOneWords() {
        Malaysia malaysia = open("Malaysia", 3, 1, 1, "T");
        assertEquals(0, malaysia.numPages);
        TextView firstWord = malaysia.findViewById(R.id.word01);
        assertEquals("cat", firstWord.getText().toString());
    }

    @Test
    public void test_italyLoteriaIsAFullLine() {
        Italy italy = open("Italy", 4, 1, 7, "T");
        assertNotNull(italy.refWord);
        assertFalse(italy.loteria());
        for (int card : new int[]{1, 2, 3, 4}) {
            italy.boardCardsFound[card - 1] = true;
        }
        assertTrue(italy.loteria());
    }

    @Test
    public void test_chinaIsSolvedWhenEveryLineIs() {
        China china = Robolectric.buildActivity(China.class).get();
        china.solvedLines = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE};
        assertFalse(china.areAllLinesSolved());
        china.solvedLines[3] = Boolean.TRUE;
        assertTrue(china.areAllLinesSolved());
    }

    @Test
    public void test_myanmarWordSearchSteps() {
        Myanmar myanmar = Robolectric.buildActivity(Myanmar.class).get();
        assertTrue(myanmar.isAdjacent(0, 1));
        assertTrue(myanmar.isAdjacent(0, 8));
        assertFalse(myanmar.isAdjacent(0, 2));
        assertFalse(myanmar.isAdjacent(0, 0));
        assertEquals(1, myanmar.getDirection(0, 1));
        assertEquals(2, myanmar.getDirection(0, 7));
        assertEquals(3, myanmar.getDirection(0, 8));
        assertEquals(4, myanmar.getDirection(1, 7));
    }

    @Test
    public void test_thailandDoesNotRepeatTheSamePrompt() {
        Thailand thailand = Robolectric.buildActivity(Thailand.class).get();
        thailand.refTile = Start.tileList.get(0);
        assertTrue(thailand.verifyFreshTile("ship", 0));
        assertFalse(thailand.verifyFreshTile(thailand.refTile.text, 0));
        assertTrue(thailand.verifyFreshTile(thailand.refTile.text, 26));
    }

    @Test
    public void test_tileAudioIsSkippedForThePlaceholderName() {
        Brazil brazil = Robolectric.buildActivity(Brazil.class).get();
        Start.Tile tile = Start.tileList.get(0);
        String original = tile.audioForThisTileType;
        try {
            assertTrue(brazil.tileShouldPlayAudio(tile));
            brazil.mediaPlayerIsPlaying = true;
            assertFalse(brazil.isReadyToPlayTileAudio());
            brazil.mediaPlayerIsPlaying = false;
            assertTrue(brazil.isReadyToPlayTileAudio());
            tile.audioForThisTileType = "X";
            assertFalse(brazil.tileShouldPlayAudio(tile));
        } finally {
            tile.audioForThisTileType = original;
        }
    }

    private static <T extends GameActivity> T open(String country, int gameNumber, int level, int stage, String mode) {
        return FixturePack.open(gameClass(country),
                FixturePack.gameIntent(1, level, stage, mode, gameNumber, country));
    }

    @SuppressWarnings("unchecked")
    private static <T extends GameActivity> Class<T> gameClass(String country) {
        try {
            return (Class<T>) Class.forName("org.alphatilesapps.alphatiles." + country);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static List<String> stageNames(int stageNumber) {
        ArrayList<String> names = new ArrayList<>();
        for (Start.Word word : Start.wordStagesLists.get(stageNumber - 1)) {
            names.add(word.wordInLOP);
        }
        return names;
    }
}
