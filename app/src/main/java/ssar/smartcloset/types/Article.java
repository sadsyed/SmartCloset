package ssar.smartcloset.types;

import java.util.Date;
import java.util.List;

/**
 * Created by ssyed on 11/9/14.
 */
public class Article {
    private String name;
    private String id;
    private String type;
    private String imageUrl;
    private Date lastUsed;
    private Date timeUsed;
    private List<String> tags;
    private Double price;
    private Boolean availableForSale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAvailableForSale() {
        return availableForSale;
    }

    public void setAvailableForSale(Boolean availableForSale) {
        this.availableForSale = availableForSale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Date timeUsed) {
        this.timeUsed = timeUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
