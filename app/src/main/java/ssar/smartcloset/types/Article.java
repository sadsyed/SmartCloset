package ssar.smartcloset.types;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by ssyed on 11/9/14.
 */
public class Article implements CustomListItem, Parcelable{
    private String articleName;
    private String articleId;
    private String articleOwner;
    private String articleType;
    private String articleImageUrl;
    private String[] articleLastUsed;
    private int articleTimesUsed;
    private List<String> articleTags;
    private Float articlePrice;
    private String articleDescription;
    private Boolean articleOkToSell;


    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String name) {
        this.articleName = name;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String id) {
        this.articleId = id;
    }

    public String getArticleOwner() {
        return articleOwner;
    }

    public void setArticleOwner(String owner) {
        this.articleOwner = owner;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String type) {
        this.articleType = type;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public void setArticleImageUrl(String imageUrl) {
        this.articleImageUrl = imageUrl;
    }

    public String[] getArticleLastUsed() {
        return articleLastUsed;
    }

    public void setArticleLastUsed(String[] articleLastUsed) {
        this.articleLastUsed = articleLastUsed;
    }

    public Float getArticlePrice() {
        return articlePrice;
    }

    public void setArticlePrice(Float price) {
        this.articlePrice = price;
    }

    public List<String> getTags() {
        return articleTags;
    }

    public void setArticleTags(List<String> tags) {
        this.articleTags = tags;
    }

    public int getArticleTimesUsed() {
        return articleTimesUsed;
    }

    public void setArticleTimesUsed(int timeUsed) {
        this.articleTimesUsed = timeUsed;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String description) {
        this.articleDescription = description;
    }

    public Boolean getArticleOkToSell() {
        return articleOkToSell;
    }

    public void setArticleOkToSell(Boolean oktosell) {
        this.articleOkToSell = oktosell;
    }

    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();

        toStringBuilder.append("Article Name: ").append(articleName)
                .append(", Article Id: ").append(articleId)
                .append(", Article Owner: ").append(articleName)
                .append(", Article Type: ").append(articleType);

        StringBuilder lastUsedDateBuilder = new StringBuilder();
        lastUsedDateBuilder.append(", Article LastUsedDate: ");
        for (String str : articleLastUsed) {
            lastUsedDateBuilder.append("[").append(str).append("]");
        }

        return toStringBuilder.append(lastUsedDateBuilder).toString();
    }

    public String getItemName() {
        return getArticleName();
    }

    public String getItemImageURL() {
        return getArticleImageUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
