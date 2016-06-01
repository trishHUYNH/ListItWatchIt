package thuynh90.tacoma.uw.edu.listitwatchit;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import thuynh90.tacoma.uw.edu.listitwatchit.tabs.Movie;

/**
 * Testing for Movie class.
 */
public class MovieTest extends TestCase {

    private Movie mMovie;

    @Test
    public void testConstructor() {
        Movie testMovie = new Movie("Some Movie Title", "1234567890");
        assertNotNull(testMovie);
    }

    @Before
    public void setUp() {
        mMovie = new Movie("Movie Title Test", "00000000");
    }

    @After
    protected void tearDown() throws Exception {
        mMovie = null;
        assertNull(mMovie);
    }

    @Test
    public void testSetNullMovieId() {
        try {
            mMovie.setMovieID(null);
            Assert.fail("Movie ID can be set to null");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetMovieId() {
       assertEquals(mMovie.getMovieID(), "00000000");
    }

    @Test (expected= IllegalArgumentException.class)
    public void testSetNullMovieTitle() {
        try {
            mMovie.setMovieTitle(null);
            Assert.fail("Movie title can be set to null");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetMovieTitle() {
        assertEquals(mMovie.getMovieTitle(), "Movie Title Test");
    }

    @Test
    public void testParseCourseJSON() {
        String courseJSON = "[{\"Title\":\"A Test Movie\",\"MovieID\":\"0987654321\"}]";
        String parsedJSON =  Movie.parseMovieJSON(courseJSON, new ArrayList<Movie>());
        assertTrue("JSON With Valid String", parsedJSON == null);
    }
}
