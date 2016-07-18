package com.examples.pj.einkaufsapp.util;

import android.view.Menu;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.fragments.AboutFragment;
import com.examples.pj.einkaufsapp.fragments.BaseFragment;
import com.examples.pj.einkaufsapp.fragments.CurrentListFragment;
import com.examples.pj.einkaufsapp.fragments.HistoricListsFragment;
import com.examples.pj.einkaufsapp.fragments.StatisticFragment;

import java.util.ArrayList;
import java.util.List;

//MANDATORY CLASS #3
public class NavigationManager {
    public static final String LOG_TAG = NavigationManager.class.getSimpleName();

    private static List<NavigationEventListener> navListeners = new ArrayList<>();

    private Menu menu;

    public NavigationManager() {
    }

    //================================================================================
    // Navigation
    //================================================================================

    public static void showFragment(BaseFragment fragment) {
        showFragment(fragment, 0);
    }

    public static void showFragment(BaseFragment fragment, int menuResId) {
        for (NavigationEventListener el : navListeners) {
            if (el != null) {
                el.switchContent(fragment);
                el.onPostSwitchFragment(menuResId);
            }
        }
    }

    public static void goBackFrom(BaseFragment fragment) {
        for (NavigationEventListener el : navListeners) {
            if (el != null) {
                el.goBackFrom(fragment);
            }
        }
    }

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
            case R.id.sidebar_exit:
                System.exit(0);
                break;
            //case R.id.ft_streez_sidebar_support_tv:
            //showFragment(getSupportFragment(), menuResId);
            //break;
        }
    }

    public static void moveToCurrentListFragment() {
        showFragment(CurrentListFragment.createInstance());
    }

    public static void moveToHistoricListsFragment() {
        showFragment(HistoricListsFragment.createInstance());
    }

    public static void moveToStatisticFragment() {
        showFragment(StatisticFragment.createInstance());
    }

    public static void moveToAboutFragment() {
        showFragment(AboutFragment.createInstance());
    }

    //================================================================================
    // Interfaces
    //================================================================================

    public static void registerNavigationEventListener(NavigationEventListener listener) {
        navListeners.add(listener);
    }

    public static void unRegisterNavigationEventListener(NavigationEventListener listener) {
        navListeners.remove(listener);
    }

    public interface NavigationEventListener {

        void switchContent(BaseFragment fragment);

        void goBackFrom(BaseFragment fragment);

        void onPostSwitchFragment(int menuResId);
    }
}
