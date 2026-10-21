package org.alphatilesapps.alphatiles;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;

import org.robolectric.Robolectric;

import java.util.HashMap;

/**
 * Loads the fixture language the way {@link Start#onCreate} does, without the
 * sound pool, the shuffles, or the loading screen. Screen tests can ask for a
 * quiet sound pool afterwards so a game can open without audio files.
 */
public final class FixturePack {

    private static Start start;

    private FixturePack() {
    }

    public static void load() {
        resetStaticLanguageState();
        start = Robolectric.buildActivity(Start.class).get();

        start.buildLangInfoList();
        start.buildKeyList();
        start.buildSettingsList();
        start.buildColorList();

        Start.hasTileAudio = setting("Has tile audio", false);
        Start.differentiatesTileTypes = setting("Differentiates types of multitype symbols", false);
        Start.hasSyllableAudio = setting("Has syllable audio", false);
        Start.sendAnalytics = setting("Send analytics", false);
        Start.changeArrowColor = setting("Change arrow colors", true);
        Start.enhancedAudioLoadingLog = setting("Enhanced Audio Loading Log", false);

        String after12 = Start.settingsList.find("After 12 checked trackers");
        Start.after12checkedTrackers = after12.equals("") ? 3 : Integer.parseInt(after12);

        String avatars = Start.settingsList.find("Number of avatars");
        if (!avatars.equals("")) {
            Start.numberOfAvatars = Integer.parseInt(avatars);
        }

        String ratio = Start.settingsList.find("Stage correspondence ratio");
        Start.stageCorrespondenceRatio = ratio.equals("") ? 0.5 : Double.parseDouble(ratio);

        start.buildTileList();
        start.distributeTilesIntoTypeLists();
        start.buildWordList();
        start.buildTileStagesLists();
        start.buildWordStagesLists();
        start.buildGameList();

        if (Start.hasSyllableGames) {
            start.buildSyllableList();
            for (int d = 0; d < Start.syllableList.size(); d++) {
                Start.SYLLABLES.add(Start.syllableList.get(d).text);
            }
        }
    }

    public static void rebuildWordStages() {
        start.buildWordStagesLists();
    }

    /** A sound pool that records nothing, so games can open without clip files. */
    public static void prepareAudioStubs() {
        Start.wordAudioIDs = new HashMap<>();
        Start.tileAudioIDs = new HashMap<>();
        Start.syllableAudioIDs = new HashMap<>();
        Start.tileDurations = new HashMap<>();
        if (Start.wordList != null) {
            for (Start.Word word : Start.wordList) {
                Start.wordAudioIDs.put(word.wordInLWC, 1);
            }
        }
        if (Start.tileList != null) {
            for (Start.Tile tile : Start.tileList) {
                Start.tileAudioIDs.put(tile.audioName, 1);
                Start.tileAudioIDs.put(tile.audioNameB, 1);
                Start.tileAudioIDs.put(tile.audioNameC, 1);
                Start.tileAudioIDs.put(tile.audioForThisTileType, 1);
                Start.tileDurations.put(tile.audioForThisTileType, 1);
            }
        }
        if (Start.syllableList != null) {
            for (Start.Syllable syllable : Start.syllableList) {
                Start.syllableAudioIDs.put(syllable.audioName, 1);
            }
        }
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        Start.gameSounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(1)
                .build();
        Start.correctSoundID = 1;
        Start.incorrectSoundID = 1;
        Start.correctFinalSoundID = 1;
        Start.correctSoundDuration = 1;
    }

    public static Intent gameIntent(int playerNumber, int challengeLevel, int stage, String mode, int gameNumber, String country) {
        Intent intent = new Intent();
        intent.putExtra("playerNumber", playerNumber);
        intent.putExtra("challengeLevel", challengeLevel);
        intent.putExtra("stage", stage);
        intent.putExtra("syllableGame", mode);
        intent.putExtra("gameNumber", gameNumber);
        intent.putExtra("country", country);
        intent.putExtra("studentGrade", '1');
        return intent;
    }

    public static <T extends Activity> T open(Class<T> type, Intent intent) {
        prepareAudioStubs();
        return Robolectric.buildActivity(type, intent).setup().get();
    }

    private static boolean setting(String label, boolean defaultValue) {
        String desired = Start.settingsList.find(label);
        if (!desired.equals("")) {
            return Boolean.parseBoolean(desired);
        }
        return defaultValue;
    }

    private static void resetStaticLanguageState() {
        Start.tileList = null;
        Start.tileListNoSAD = null;
        Start.wordList = null;
        Start.tileStagesLists = null;
        Start.wordStagesLists = null;
        Start.tileHashMap = null;
        Start.tileHashMapNoSAD = null;
        Start.lopWordHashMap = null;
        Start.lwcWordHashMap = null;
        Start.syllableList = null;
        Start.syllableHashMap = null;
        Start.settingsList = null;
        Start.langInfoList = null;
        Start.gameList = null;
        Start.keyList = null;
        Start.nameList = null;
        Start.localAppName = null;
        Start.scriptType = null;
        Start.placeholderCharacter = null;
        Start.differentiatesTileTypes = null;
        Start.hasTileAudio = null;
        Start.hasSyllableAudio = null;
        Start.sendAnalytics = null;
        Start.enhancedAudioLoadingLog = null;
        Start.changeArrowColor = true;
        Start.stageCorrespondenceRatio = 0;
        Start.after12checkedTrackers = 0;
        Start.numberOfAvatars = 12;
        Start.hasSyllableGames = false;
        Start.hasSAD = false;
        Start.wordAudioIDs = null;
        Start.tileAudioIDs = null;
        Start.syllableAudioIDs = null;
        Start.tileDurations = null;
        Start.gameSounds = null;
        Start.colorList.clear();
        Start.boxColors.clear();
        Start.CONSONANTS.clear();
        Start.PLACEHOLDER_CONSONANTS.clear();
        Start.SILENT_PRELIMINARY_TILES.clear();
        Start.SILENT_PLACEHOLDER_CONSONANTS.clear();
        Start.VOWELS.clear();
        Start.CorV.clear();
        Start.TONES.clear();
        Start.ADs.clear();
        Start.Ds.clear();
        Start.SAD.clear();
        Start.SYLLABLES.clear();
        Start.SAD_STRINGS.clear();
        Start.MULTITYPE_TILES.clear();
    }
}
