package com.examples.pj.einkaufsapp;

import android.view.View;

import com.examples.pj.einkaufsapp.fragments.BaseFragment;
import com.examples.pj.einkaufsapp.fragments.CurrentListFragment;
import com.examples.pj.einkaufsapp.util.NavigationManager;

//MANDATORY CLASS #1
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
//        toast("Ich bin im onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        toast("Ich bin im onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        toast("Ich bin im onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        toast("Ich bin im onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        toast("Ich bin im onDestroy");
    }

    //================================================================================
    // Butterknife Events
    //================================================================================

    public void onMenuLeftItemSelected(View v) {
        NavigationManager.switchToFragmentForMenuResId(v.getId());
    }

    //================================================================================
    // Other Methods
    //================================================================================

    @Override
    public void onPostSwitchFragment(int menuResId) {
    }

}
