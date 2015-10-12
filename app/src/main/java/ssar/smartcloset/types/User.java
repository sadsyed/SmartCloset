package ssar.smartcloset.types;

/**
 * Created by ssyed on 11/29/14.
 */
public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private String userEmail;
    private String userPin;
    private String userPassword;
    private String userMarkdown;
    private String tokenId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPin() {
        return userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserMarkdown() {
        return userMarkdown;
    }

    public void setUserMarkdown(String userMarkdown) {
        this.userMarkdown = userMarkdown;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String toString() {
        StringBuilder user = new StringBuilder();

        user.append("User Name: ").append(userName)
                .append("First Name: ").append(firstName)
                .append("Last Name: ").append(lastName)
                .append("User Email: ").append(userName)
                .append("User Pin: ").append(userPin)
                .append("User Markdown: ").append(userMarkdown)
                .append("Token Id: ").append(tokenId);

        return user.toString();
    }
}
