package ssar.smartcloset.types;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import ssar.smartcloset.BaseSearchFragment;
import ssar.smartcloset.NeverUsedFragment;
import ssar.smartcloset.SellFilterFragment;
import ssar.smartcloset.TagSearchFragment;
import ssar.smartcloset.UsageFilterFragment;
import ssar.smartcloset.util.SmartClosetConstants;

/**
 * Created by ssyed on 11/30/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    //number of ViewPager pages
    final int PAGE_COUNT = 5;
    private String tabTitles[] =
            new String[] {SmartClosetConstants.SEARCHTAB_BASE_SEARCH,
            SmartClosetConstants.SEARCHTAB_USAGE_FILTER,
            SmartClosetConstants.SEARCHTAB_TAG_FILTER,
            SmartClosetConstants.SEARCHTAB_NEVER_USED_FILTER,
            SmartClosetConstants.SEARCHTAB_OK_TO_SELL};

    public ViewPagerAdapter (FragmentManager fragmentManager) {
        super (fragmentManager);
    }

    @Override
    public Fragment getItem (int position) {
        switch (position) {
            //open String Search fragment
            case 0:
                BaseSearchFragment baseSearchFragment = new BaseSearchFragment();
                return baseSearchFragment;

            case 1:
                UsageFilterFragment usageFilterFragment = new UsageFilterFragment();
                return usageFilterFragment;

            case 2:
                TagSearchFragment tagSearchFrgament = new TagSearchFragment();
                return tagSearchFrgament;

            case 3:
                NeverUsedFragment neverUsedFragment = new NeverUsedFragment();
                return neverUsedFragment;

            case 4:
                SellFilterFragment sellFilterFragment = new SellFilterFragment();
                return sellFilterFragment;
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
