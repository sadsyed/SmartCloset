package ssar.smartcloset.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ssyed on 11/24/14.
 */
public class Category implements CustomListItem, Parcelable{
    private String name;
    private String lastUsedArticleImageUrl;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastUsedArticleImageUrl() {
        return lastUsedArticleImageUrl;
    }

    public void setLastUsedArticleImageUrl(String lastUsedArticleImageUrl) {
        this.lastUsedArticleImageUrl = lastUsedArticleImageUrl;
    }

    public String getItemName() {
        return getName();
    }

    public String getItemImageURL() {
        return getLastUsedArticleImageUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
