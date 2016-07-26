package com.examples.pj.einkaufsapp.util;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.fragments.AboutFragment;
import com.examples.pj.einkaufsapp.fragments.BaseFragment;
import com.examples.pj.einkaufsapp.fragments.CurrentListFragment;
import com.examples.pj.einkaufsapp.fragments.HistoricListsFragment;
import com.examples.pj.einkaufsapp.fragments.StatisticFragment;
import com.examples.pj.einkaufsapp.fragments.TestFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all possible Navigation of App
 */
public class NavigationManager {
    public static final String LOG_TAG = NavigationManager.class.getSimpleName();

    private static List<NavigationEventListener> navListeners = new ArrayList<>();

    //================================================================================
    // Navigation
    //================================================================================

    /**
     * Show called Fragment
     *
     * @param fragment
     */
    public static void showFragment(BaseFragment fragment) {
        showFragment(fragment, 0);
    }

    /**
     * Show called Fragment by menu selection
     *
     * @param fragment
     * @param menuResId
     */
    public static void showFragment(BaseFragment fragment, int menuResId) {
        for (NavigationEventListener el : navListeners) {
            if (el != null) {
                el.switchContent(fragment);
                el.onPostSwitchFragment(menuResId);
            }
        }
    }

    /**
     * move to last Fragment
     *
     * @param fragment
     */
    public static void goBackFrom(BaseFragment fragment) {
        for (NavigationEventListener el : navListeners) {
            if (el != null) {
                el.goBackFrom(fragment);
            }
        }
    }

    /**
     * if menu item is selected, move to fragment
     *
     * @param menuResId
     */
    public static void switchToFragmentForMenuResId(int menuResId) {
        switch (menuResId) {
            case R.id.sidebar_currentlist_screen:
                moveToCurrentListFragment();
                break;
            case R.id.sidebar_historiclist_screen:
                moveToHistoricListsFragment();
                break;
            case R.id.sidebar_statistics_screen:
                moveToStatisticFragment();
                break;
            case R.id.sidebar_about_screen:
                moveToAboutFragment();
                break;
            case R.id.sidebar_test_screen:
                moveToTestFragment();
                break;
            case R.id.sidebar_exit:
                System.exit(0);
                break;
            default:
        }
    }

    /**
     * moveToCurrentListFragment
     */
    public static void moveToCurrentListFragment() {
        showFragment(CurrentListFragment.createInstance());
    }

    /**
     * moveToHistoricListsFragment
     */
    public static void moveToHistoricListsFragment() {
        showFragment(HistoricListsFragment.createInstance());
    }

    /**
     * moveToStatisticFragment
     */
    public static void moveToStatisticFragment() {
        showFragment(StatisticFragment.createInstance());
    }

    /**
     * moveToAboutFragment
     */
    public static void moveToAboutFragment() {
        showFragment(AboutFragment.createInstance());
    }

    /**
     * moveToTestFragment
     */
    public static void moveToTestFragment() {
        showFragment(TestFragment.createInstance());
    }

    //================================================================================
    // Interfaces
    //================================================================================

    /**
     * register listener
     *
     * @param listener
     */
    public static void registerNavigationEventListener(NavigationEventListener listener) {
        navListeners.add(listener);
    }

    /**
     * unregister listener
     *
     * @param listener
     */
    public static void unRegisterNavigationEventListener(NavigationEventListener listener) {
        navListeners.remove(listener);
    }

    /**
     * Navigation Event Listener
     */
    public interface NavigationEventListener {
        /**
         * switchContent
         *
         * @param fragment
         */
        void switchContent(BaseFragment fragment);

        /**
         * goBackFrom
         *
         * @param fragment
         */
        void goBackFrom(BaseFragment fragment);

        /**
         * what happens after fragment switch
         *
         * @param menuResId
         */
        void onPostSwitchFragment(int menuResId);
    }
}
