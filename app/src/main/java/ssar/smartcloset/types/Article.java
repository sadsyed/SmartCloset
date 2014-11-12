package ssar.smartcloset.types;

import java.util.Date;
import java.util.List;

/**
 * Created by ssyed on 11/9/14.
 */
public class Article {
    private String articleName;
    private String articleId;
    private String articleOwner;
    private String articleType;
    private String articleImageUrl;
    private Date articleLastUsed;
    private List<Date> articleTimesUsed;
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

    public Date getArticleLastUsed() {
        return articleLastUsed;
    }

    public void setArticleLastUsed(Date lastUsed) {
        this.articleLastUsed = lastUsed;
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

    public List<Date> getArticleTimesUsed() {
        return articleTimesUsed;
    }

    public void setArticleTimesUsed(List<Date> timeUsed) {
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
}
