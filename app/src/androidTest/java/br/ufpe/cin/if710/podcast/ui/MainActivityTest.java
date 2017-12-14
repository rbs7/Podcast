package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.R;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by jpvic on 13/12/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> test = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testButtonLabelChange() {
        Espresso.onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(0)
            .onChildView(withId(R.id.item_action))
            .perform(click());

        Espresso.onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(0)
            .onChildView(withId(R.id.item_action))
            .check(matches(withText("baixando")));
    }

}