package ssar.smartcloset.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.Category;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.types.User;

/**
 * Created by ssyed on 11/24/14.
 */
public class JsonParserUtil {
    private final static String CLASSNAME = JsonParserUtil.class.getSimpleName();

    public static List<CustomListItem> jsonToCategory(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        List<CustomListItem> categories = new ArrayList<CustomListItem>();

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
                    CustomListItem customListItem = gson.fromJson(jsonArray.getJSONObject(i).toString(), Category.class);
                    //Category categoryObj = gson.fromJson(jsonArray.getJSONObject(i).toString(), Category.class);
                    categories.add(customListItem);
                }
            } else {
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " jsonArray empty");
                return null;
            }
        } catch (JSONException e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + "e");
        }
        return categories;
    }

    public static List<CustomListItem> jsonToArticle(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        List<CustomListItem> articles = new ArrayList<CustomListItem>();

        try{
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);
            JSONArray jsonArray = new JSONArray();

            switch(serviceURL) {
                case SmartClosetConstants.GET_CATEGORY:
                    jsonArray = json.getJSONArray("category");
                    articles = parseJsonArraytoArticle(gson, articles, jsonArray);
                    break;

                case SmartClosetConstants.SEARCH_ARTICLES:
                    jsonArray = json.getJSONArray("articleList");
                    articles = parseJsonArraytoArticle(gson, articles, jsonArray);
                    break;

                case SmartClosetConstants.READ_ARTICLE:
                    CustomListItem customListItem = gson.fromJson(json.toString(), Article.class);
                    articles.add(customListItem);
            }

        } catch (JSONException e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        }
        return articles;
    }

    private static List<CustomListItem> parseJsonArraytoArticle(Gson gson, List<CustomListItem> articles, JSONArray jsonArray) throws JSONException {
        List<CustomListItem> customList = new ArrayList<CustomListItem>();

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Parse article JSON to Java article object: " + jsonArray.toString());
        if(jsonArray.length() > 0) {
            for(int i=0; i<jsonArray.length(); i++) {
                CustomListItem customListItem = gson.fromJson(jsonArray.getJSONObject(i).toString(), Article.class);
                customList.add(customListItem);
            }
        } else {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " jsonArray empty");
            return null;
        }
        return customList;
    }

    public static User jsonToUser(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        User user = new User();

        try{
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);
            JSONArray jsonArray = new JSONArray();

            switch(serviceURL) {
                case SmartClosetConstants.GET_USER_ACCOUNT:
                    user = gson.fromJson(json.toString(), User.class);
                    break;
            }

        } catch (JSONException e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        }
        return user;
    }
}
