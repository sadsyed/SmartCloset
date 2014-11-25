package ssar.smartcloset.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.smartcloset.CategoryFragment;
import ssar.smartcloset.types.Category;

/**
 * Created by ssyed on 11/24/14.
 */
public class JsonParserUtil {
    private final static String CLASSNAME = JsonParserUtil.class.getSimpleName();

    public static List<Category> jsonToCategory(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        List<Category> categories = new ArrayList<Category>();

        try{
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);
            JSONArray jsonArray = new JSONArray();

            switch(serviceURL) {
                case SmartClosetConstants.GET_CATEGORIES:
                       jsonArray = json.getJSONArray("currentCategories");
                    break;
            }

            Log.i(CLASSNAME, CLASSNAME + ": Parse category JSON to Java category object:");
            if(jsonArray.length() > 0) {
                for(int i=0; i<jsonArray.length(); i++) {
                    Category categoryObj = gson.fromJson(jsonArray.getJSONObject(i).toString(), Category.class);
                    categories.add(categoryObj);
                }
            } else {
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + "jsonArray empty");
            }
        } catch (JSONException e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + "e");
        }
        return categories;
    }
}
