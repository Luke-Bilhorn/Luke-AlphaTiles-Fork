package org.alphatilesapps.alphatiles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

/** Stage assignment after the defaults in the fixture settings file. */
@RunWith(RobolectricTestRunner.class)
public class StageRulesTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_lowerRatioLetsAPartialWordInEarlier() {
        Start.stageCorrespondenceRatio = 0.3;
        Start.settingsList.put("First letter stage correspondence", "false");
        Start.settingsList.put("Stage 1-2 max word length", "99");
        FixturePack.rebuildWordStages();

        assertTrue(stageNames(1).contains("ship"));
    }

    @Test
    public void test_firstLetterKeepsThatWordBack() {
        Start.stageCorrespondenceRatio = 0.3;
        Start.settingsList.put("First letter stage correspondence", "true");
        Start.settingsList.put("Stage 1-2 max word length", "99");
        FixturePack.rebuildWordStages();

        assertFalse(stageNames(1).contains("ship"));
        assertTrue(stageNames(2).contains("ship"));
    }

    @Test
    public void test_longWordsWaitUntilStageThree() {
        Start.settingsList.put("Stage 1-2 max word length", "2");
        FixturePack.rebuildWordStages();

        assertFalse(stageNames(1).contains("cat"));
        assertTrue(stageNames(1).contains("เก"));
        assertTrue(stageNames(2).contains("cat"));
    }

    @Test
    public void test_explicitStageOverridesTheRatio() {
        Start.stageCorrespondenceRatio = 0.3;
        FixturePack.rebuildWordStages();

        assertTrue(stageNames(4).contains("ka.ke"));
        assertFalse(stageNames(1).contains("ka.ke"));
    }

    private static List<String> stageNames(int stageNumber) {
        ArrayList<String> names = new ArrayList<>();
        for (Start.Word word : Start.wordStagesLists.get(stageNumber - 1)) {
            names.add(word.wordInLOP);
        }
        return names;
    }
}
