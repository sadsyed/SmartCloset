package ssar.smartcloset.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ssyed on 11/24/14.
 */
public class Category implements Parcelable{
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
