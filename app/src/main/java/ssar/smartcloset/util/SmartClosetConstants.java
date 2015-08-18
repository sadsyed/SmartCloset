package ssar.smartcloset.util;

/**
 * Created by ssyed on 11/9/14.
 */
public class SmartClosetConstants {
    public static final String SMARTCLOSET_DEBUG_TAG = "SMARTCLOSET_DEBUG_TAG";

    public static final String MAINMENU_CLOSET_BUTTON = "MAINMENU_CLOSET_BUTTON";
    public static final String MAINMENU_SEARCH_BUTTON = "MAINMENU_SEARCH_BUTTON";
    public static final String MAINMENU_NEWTAG_BUTTON = "MAINMENU_NEWTAG_BUTTON";

    public static final String APP_SPOT = "http://moonlit-shadow-813.appspot.com";
    //public static final String APP_SPOT = "http://storefrontssar2.appspot.com";
    public static final String CREATE_ARTICLE = APP_SPOT + "/CreateArticle";
    public static final String UPLOAD_ARTICLE_IMAGE = APP_SPOT + "/AndroidUploadHandler";
    public static final String USE_ARTICLE = APP_SPOT + "/UseArticle";
    public static final String GET_CATEGORIES = APP_SPOT + "/GetCategories";
    public static final String GET_CATEGORY = APP_SPOT + "/GetCategory";
    public static final String CREATE_PROFILE = APP_SPOT + "/signup2";
    public static final String SEARCH_ARTICLES = APP_SPOT + "/SearchArticles";
    public static final String READ_ARTICLE = APP_SPOT + "/ReadArticle";
    public static final String UPDATE_ARTICLE = APP_SPOT + "/UpdateArticle";
    public static final String GET_USER_ACCOUNT = APP_SPOT + "/GetUserAccount";
    public static final String FIND_MATCH = APP_SPOT + "/FindMatch";
    public static final String UPDATE_ARTICLE_IMAGE_COLORS = APP_SPOT + "/UpdateArticleImageColors";


    //Amazon Elastic Beanstalk service url
    public static final String AMAZON_APP_SPOT = "http://eb-django-app-test.elasticbeanstalk.com";
    public static final String EXTRACT_ARTICLE_IMAGE_COLORS = AMAZON_APP_SPOT + "/removebackground/";

    public static final String PROCESS_RESPONSE = "ssar.intent.action";

    //SlideMenu Constants
    public static final int SLIDEMENU_HOME_ITEM = 0;
    public static final int SLIDEMENU_CLOSET_ITEM = 1;
    public static final int SLIDEMENU_SEARCH_ITEM = 2;
    public static final int SLIDEMENU_NEWTAG_ITEM = 3;
    public static final int SLIDEMENU_PROFILE_ITEM = 4;
    public static final int SLIDEMENU_ARTICLE_ITEM = 5;
    public static final int SLIDEMENU_MATCH_ITEM = 6;

    //Profile Shared Preference
    public static final String PREF_NAME = "smartProfilePreference";
    public static final String SHAREDPREFERENCE_USER_NAME = "UserName";
    public static final String SHAREDPREFERENCE_FIRST_NAME = "FirstName";
    public static final String SHAREDPREFERENCE_LAST_NAME = "LastName";
    public static final String SHAREDPREFERENCE_EMAIL = "Email";
    public static final String SHAREDPREFERENCE_PASSWORD = "Password";
    public static final String SHAREDPREFERENCE_PIN = "Pin";

    public static final String SEARCHTAB_BASE_SEARCH = "Base Search";
    public static final String SEARCHTAB_USAGE_FILTER = "Usage Filter";
    public static final String SEARCHTAB_TAG_FILTER = "Tag Filter";
    public static final String SEARCHTAB_NEVER_USED_FILTER = "Never Used Filter";
    public static final String SEARCHTAB_OK_TO_SELL = "Selling Filter";
}
