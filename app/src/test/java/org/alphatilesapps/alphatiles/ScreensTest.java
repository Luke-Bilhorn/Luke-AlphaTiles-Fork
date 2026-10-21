package org.alphatilesapps.alphatiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Opens each screen that can run without recorded clips and checks the text
 * a player would see.
 */
@RunWith(RobolectricTestRunner.class)
public class ScreensTest {

    @Before
    public void loadFixturePack() {
        FixturePack.load();
    }

    @Test
    public void test_startOpensTheLoadingScreen() {
        Start start = Robolectric.buildActivity(Start.class).setup().get();
        Intent next = shadowOf(start).getNextStartedActivity();
        assertEquals(LoadingScreen.class.getName(), next.getComponent().getClassName());
        assertNotNull(start.gameSounds);
        assertNotNull(Chile.data);
        assertFalseEmpty(Chile.data.words);
    }

    @Test
    public void test_aboutShowsTheGameName() {
        About about = FixturePack.open(About.class, new Intent());
        TextView name = about.findViewById(R.id.gameNameInLOP);
        assertEquals("Fixture", name.getText().toString());
    }

    @Test
    public void test_shareDrawsTheLink() {
        Share share = FixturePack.open(Share.class, new Intent());
        ImageView code = share.findViewById(R.id.qrCode);
        assertNotNull(code.getDrawable());
    }

    @Test
    public void test_celebrationOpens() {
        Celebration celebration = FixturePack.open(Celebration.class, new Intent());
        assertNotNull(celebration.findViewById(R.id.celebrationCL));
    }

    @Test
    public void test_resourcesShowsThePackLink() {
        org.alphatilesapps.alphatiles.Resources resources =
                FixturePack.open(org.alphatilesapps.alphatiles.Resources.class, new Intent());
        TextView label = resources.findViewById(R.id.resourceText01);
        assertEquals("Stories", label.getText().toString());
    }

    @Test
    public void test_choosePlayerShowsTheCustomNames() {
        ChoosePlayer choosePlayer = FixturePack.open(ChoosePlayer.class, new Intent());
        TextView first = choosePlayer.findViewById(ChoosePlayer.AVATAR_NAMES[0]);
        assertEquals("Ana", first.getText().toString());
    }

    @Test
    public void test_setPlayerNameTypesOnThePackKeyboard() {
        SetPlayerName nameScreen = FixturePack.open(SetPlayerName.class,
                FixturePack.gameIntent(1, 1, 1, "T", 1, "Brazil"));
        TextView name = nameScreen.findViewById(R.id.avatarName);
        assertEquals("Ben", name.getText().toString());
        nameScreen.deleteLastKeyedLetter(null);
        assertEquals("Be", name.getText().toString());

        TextView key = new TextView(nameScreen);
        key.setTag("1");
        nameScreen.onBtnClick(key);
        assertEquals("Bec", name.getText().toString());
    }

    @Test
    public void test_earthNumbersTheDoorsFromTheGameList() {
        Earth earth = FixturePack.open(Earth.class,
                FixturePack.gameIntent(1, 1, 1, "T", 1, "Brazil"));
        TextView player = earth.findViewById(R.id.avatarName);
        assertEquals("Ana", player.getText().toString());
        TextView firstDoor = door(earth, "1");
        TextView pastTheList = door(earth, "7");
        assertEquals("1", firstDoor.getText().toString());
        assertEquals(android.view.View.INVISIBLE, pastTheList.getVisibility());
    }

    private static TextView door(Earth earth, String tag) {
        for (int j = 0; j < earth.earthCL.getChildCount(); j++) {
            android.view.View child = earth.earthCL.getChildAt(j);
            if (child instanceof TextView && tag.equals(child.getTag())) {
                return (TextView) child;
            }
        }
        throw new AssertionError("earth has no door " + tag);
    }

    private static void assertFalseEmpty(java.util.List<?> list) {
        org.junit.Assert.assertFalse(list.isEmpty());
    }
}
