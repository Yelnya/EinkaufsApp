package com.examples.pj.einkaufsapp;

import android.view.View;

import com.examples.pj.einkaufsapp.fragments.BaseFragment;
import com.examples.pj.einkaufsapp.fragments.CurrentListFragment;
import com.examples.pj.einkaufsapp.util.NavigationManager;

/**
 * MainActivity class
 */
public class MainActivity extends BaseActivity {

    NavigationManager navigationManager;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseFragment getStartFragment() {
        return CurrentListFragment.createInstance();
    }

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    protected void onStart() {
        super.onStart();
        navigationManager = new NavigationManager();
    }
    //================================================================================
    // Butterknife Events
    //================================================================================

    /**
     * Navigation behaviour on item click of left Side Menu
     *
     * @param v: view
     */
    public void onMenuLeftItemSelected(View v) {
        NavigationManager.switchToFragmentForMenuResId(v.getId());
    }

    //================================================================================
    // Other Methods
    //================================================================================

    @Override
    public void onPostSwitchFragment(int menuResId) {
        //not needed
    }

}
