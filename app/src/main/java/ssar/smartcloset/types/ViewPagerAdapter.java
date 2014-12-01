package ssar.smartcloset.types;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import ssar.smartcloset.BaseSearchFragment;
import ssar.smartcloset.TagSearchFragment;
import ssar.smartcloset.UsageFilterFragment;

/**
 * Created by ssyed on 11/30/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    //number of ViewPager pages
    final int PAGE_COUNT = 3;
    //private String tabTitles[] = new String[] {"Base Search", "Scan Tag", "Usage Filter", "Never Used", "Available for Selling"};
    private String tabTitles[] = new String[] {"Base Search", "Usage Filter", "Scan Tag"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
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

            //TODO case 2-4

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
