package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * MyList object class for users' custom lists. Creates an instance of MyList for each value
 * returned from JSON results.
 * TODO: Remove "Watched" & "To Watch" from results
 */
public class MyList implements Serializable {

    private String mListName;
    private String mListID;

    public static final String LIST_NAME = "ListName";
    public static final String LIST_ID = "ListID";

    /**
     * Creates a MyList object that holds a list's name and ID
     * @param listName The name of the list
     * @param listID The ID associated with the list on MySQL
     */
    public MyList(String listName, String listID) {
        setListName(listName);
        setListID(listID);
    }

    public String getListName() {
        return mListName;
    }

    public String getListID() {
        return mListID;
    }

    public void setListName(String listName) {
        this.mListName = listName;
    }

    public void setListID(String listID) {
        this.mListID = listID;
    }

    /**
     * Parses JSONObject that holds list information to retrieve the list's name & ID
     * @param listJSON
     * @param myLists
     * @return String of error. Else, returns null
     */
    public static String parseListJSON(String listJSON, List<MyList> myLists) {
        String reason = null;
        if (listJSON != null) {
            try {
                JSONArray arr = new JSONArray(listJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    MyList movieList = new MyList(obj.getString(MyList.LIST_NAME), obj.getString(MyList.LIST_ID));
                    myLists.add(movieList);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

}
