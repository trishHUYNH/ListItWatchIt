package thuynh90.tacoma.uw.edu.listitwatchit;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ImageView;

import com.robotium.solo.Solo;

/**
 * Automated Robotium class for ViewMovieDetailsActivity
 */
public class ViewMovieDetailsActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo soloTest;

    public ViewMovieDetailsActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        soloTest = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() {
        soloTest.finishOpenedActivities();
    }


    public void testViewDetails() {
        soloTest.clickOnView(getActivity().findViewById(R.id.search));
        boolean searchHintLoaded = soloTest.searchText("Search");
        assertTrue("Search bar loaded", searchHintLoaded);

        soloTest.enterText(0, "Penguins of M");
        boolean searchResultLoaded = soloTest.searchText("Penguins of Madagascar");
        assertTrue("Search result loaded", searchResultLoaded);

        soloTest.clickOnText("Penguins of Madagascar");
        boolean detailsLoaded = soloTest.searchText("MPAA Rating:");
        assertTrue("Movie details loaded", detailsLoaded);

    }

    public void testAddMovieLoggedOut() {
        soloTest.clickOnMenuItem("Logout");
        soloTest.clickOnImage(0);
        soloTest.waitForActivity("MainActivity");

        soloTest.clickOnView(getActivity().findViewById(R.id.search));
        boolean searchHintLoaded = soloTest.searchText("Search");
        assertTrue("Search bar loaded", searchHintLoaded);

        soloTest.enterText(0, "Penguins of M");
        soloTest.clickOnText("Penguins of Madagascar");
        soloTest.clickOnButton("Add to List");
        assertTrue(soloTest.waitForText("Please login"));
    }

    public void testAddMovieLoggedIn() {
        soloTest.clickOnMenuItem("Logout");
        soloTest.enterText(0, "userb@gmail.com");
        soloTest.enterText(1, "password");
        soloTest.clickOnButton("Login");

        boolean redirectedToHome = soloTest.waitForText("successful");
        assertTrue(redirectedToHome);
        soloTest.waitForActivity("MainActivity");

        soloTest.clickOnView(getActivity().findViewById(R.id.search));
        boolean searchHintLoaded = soloTest.searchText("Search");
        assertTrue("Search bar loaded", searchHintLoaded);

        soloTest.enterText(0, "Penguins of M");
        soloTest.clickOnText("Penguins of Madagascar");
        soloTest.clickOnButton("Add to List");

        soloTest.clickOnText("To Watch");
        assertTrue(soloTest.waitForText("Movie added"));
    }

}
