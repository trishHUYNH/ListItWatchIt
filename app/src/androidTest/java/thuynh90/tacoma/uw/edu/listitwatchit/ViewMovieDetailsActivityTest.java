package thuynh90.tacoma.uw.edu.listitwatchit;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

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
        boolean searchHintLoaded = soloTest.searchText("Search a movie...");
        assertTrue("Search bar loaded", searchHintLoaded);

       soloTest.enterText((EditText) getActivity().findViewById(R.id.search), "Penguins of Madagascar");

//        boolean detailsLoaded = soloTest.searchText("MPAA Rating:");
//        assertTrue("Movie details loaded", detailsLoaded);

    }
}
