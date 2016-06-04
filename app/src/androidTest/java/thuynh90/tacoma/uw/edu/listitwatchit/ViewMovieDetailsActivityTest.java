package thuynh90.tacoma.uw.edu.listitwatchit;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
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


    /**
     * Tests if the search function works and if the movie details page loads
     */
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

    /**
     * Tests if the app functions properly when a user is not logged in.
     * When the Add to List button is clicked a toast message should ask the user to log in
     */
    public void testAddMovieLoggedOut() {
        soloTest.clickOnMenuItem("Logout");
        soloTest.clickOnImage(0);
        soloTest.waitForActivity("MainActivity");

        soloTest.clickOnView(soloTest.getView(R.id.search));
        boolean searchHintLoaded = soloTest.searchText("Search");
        assertTrue("Search bar loaded", searchHintLoaded);

        soloTest.enterText(0, "Penguins of M");
        soloTest.clickOnText("Penguins of Madagascar");
        soloTest.clickOnButton("Add to List");
        assertTrue(soloTest.waitForText("Please login"));
    }

    /**
     * Tests if the app functions properly when a user is logged in.
     * When the Add to List button is clicked and a list selected,
     * the movie should be added to the list
     */
    public void testAddMovieLoggedIn() {
        soloTest.clickOnMenuItem("Logout");
        soloTest.enterText(0, "userb@gmail.com");
        soloTest.enterText(1, "password");
        soloTest.clickOnButton("Login");

        soloTest.waitForActivity("MainActivity");

        soloTest.clickOnView(soloTest.getView(R.id.search));
        boolean searchHintLoaded = soloTest.searchText("Search");
        assertTrue("Search bar loaded", searchHintLoaded);

        soloTest.enterText(0, "Penguins of M");
        soloTest.clickOnText("Penguins of Madagascar");
        soloTest.clickOnButton("Add to List");

        soloTest.clickOnText("To Watch");
        assertTrue(soloTest.waitForText("Movie added"));
    }

}
