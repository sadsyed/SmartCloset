package ssar.smartcloset.types;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ssar.smartcloset.BaseSearchFragment;
import ssar.smartcloset.TagSearchFragment;

/**
 * Created by ssyed on 11/30/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    //number of ViewPager pages
    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] {"Base Search", "Scan Tag", "Usage Filter", "Never Used", "Available for Selling"};

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
                TagSearchFragment tagSearchFrgament = new TagSearchFragment();
                return tagSearchFrgament;
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
