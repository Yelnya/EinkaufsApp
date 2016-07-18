package com.examples.pj.einkaufsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.fragments.BaseFragment;
import com.examples.pj.einkaufsapp.util.NavigationManager;

import butterknife.Bind;
import butterknife.ButterKnife;

//MANDATORY CLASS #2
public abstract class BaseActivity extends AppCompatActivity implements NavigationManager.NavigationEventListener {
    public static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private static final String SIS_CONTENT_FRAGMENT = "ContentFragment";
    protected FragmentManager fragmentManager;
    protected BaseFragment contentFragment;
    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Nullable
    @Bind(R.id.left_drawer)
    View leftDrawer;
    ActionBarDrawerToggle drawerToggle;
    private String startFragmentTag;
    TextView toolbarTv;
    ImageView toolbarEditIv;
    ImageView toolbarDeleteIv;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    protected abstract int getLayoutResId();

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);

        if (drawerLayout != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.leftsidemenu_open, R.string.leftsidemenu_close);
            drawerLayout.setDrawerListener(drawerToggle);
            drawerLayout.setFocusableInTouchMode(false);
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            contentFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, SIS_CONTENT_FRAGMENT);
        }

        if (contentFragment != null) {
            Log.d(LOG_TAG, "restored fragment from savedinstancestate");
            switchContent(contentFragment);
        } else {
            BaseFragment startFragment = getStartFragment();
            if (startFragment != null) {
                switchContent(startFragment);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart()");
        NavigationManager.registerNavigationEventListener(this);

//        toast("Ich bin im onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop()");
        NavigationManager.unRegisterNavigationEventListener(this);
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
    protected void onRestart() {
        super.onRestart();
//        toast("Ich bin im onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        toast("Ich bin im onDestroy");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        toast("Ich bin im onPostCreate");
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    //================================================================================
    // Methods
    //================================================================================

    protected BaseFragment getStartFragment() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (contentFragment != null && contentFragment.isAdded()) {
            fragmentManager.putFragment(outState, SIS_CONTENT_FRAGMENT, contentFragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
                    return true;
                } else {
                    onBackPressed();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    //================================================================================
    // Navigation
    //================================================================================

    @Override
    public void onBackPressed() {
        if (contentFragment != null) {
            if (!contentFragment.canGoBack()) {
                return;
            }

            // if user is at QuizKnowledgeResultFragment and navigates back then we have to show the RouteRootFragment
//            if (contentFragment instanceof QuizKnowledgeResultFragment) {
//                final QuizKnowledgeResultFragment fragment = (QuizKnowledgeResultFragment) contentFragment;
//                if (fragment.isRouteQuiz()) {
//                    NavigationManager.moveToRoute();
//                } else {
//                    NavigationManager.moveToQuiz();
//                }
//                return;
//            }

            // if user is at ProfileModalitiesFragment and navigates back then we have to show the RouteRootFragment
//            if (contentFragment instanceof ProfileModalitiesFragment) {
//                NavigationManager.moveToRoute();
//                return;
//            }

            if (contentFragment.isRootFragment()) {
                if (!isSlidingDrawerOpened() && isNavigationEnabled(contentFragment)) {
                    showMenue();
                } else {
                    finish();
                }
                return;
            } else {
                if (isSlidingDrawerOpened()) {
                    showMenue();
                    return;
                }
            }
        } else if (!isSlidingDrawerOpened()) {
            showMenue();
            return;
        }

        super.onBackPressed();
        setCurrentContentFragment();

        refreshDrawerIndicator();
    }

    public boolean isSlidingDrawerOpened() {
        return leftDrawer != null && drawerLayout != null && drawerLayout.isDrawerOpen(leftDrawer);
    }

    public void showMenue() {
        try {
            drawerLayout.openDrawer(GravityCompat.START);
        } catch (IllegalArgumentException e) {
        }
    }

    public void closeMenue() {
        try {
            drawerLayout.closeDrawer(GravityCompat.START);
        } catch (IllegalArgumentException e) {
        }
    }

    @Override
    public void goBackFrom(BaseFragment fragment) {
        fragmentManager.popBackStackImmediate();
        setCurrentContentFragment();
        refreshDrawerIndicator();
    }

    @Override
    public void switchContent(BaseFragment fragment) {
        // do not change fragment if we want to change to same fragment
        if (contentFragment == null || !contentFragment.getBaseTag().equals(fragment.getBaseTag())) {
            if (fragment.isRootFragment()) {
                fragmentManager.popBackStackImmediate(startFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            FragmentTransaction tx = fragmentManager.beginTransaction();
            fragment.setCustomAnimation(tx);
            tx.replace(R.id.activity_main_fragment_container, fragment, fragment.getBaseTag());
            if (fragment.isRootFragment()) {
                startFragmentTag = fragment.getBaseTag();
            }
            tx.addToBackStack(fragment.getBaseTag());

            if (drawerToggle != null) {
                drawerToggle.setDrawerIndicatorEnabled(fragment.isRootFragment());
            }
            contentFragment = fragment;
            tx.commit();
        } else {
        }
        closeMenue();
    }

    private void setCurrentContentFragment() {
        // get current stack fragment visible to save it in instance state when the activity quits
        String currentFragmentBaseTag;

        final int lastItemIndex = fragmentManager.getBackStackEntryCount() - 1;
        if (lastItemIndex >= 0) {
            currentFragmentBaseTag = fragmentManager.getBackStackEntryAt(lastItemIndex).getName();
        } else { // we are at the end of the line - quit
            finish();
            return;
        }
        // might be null if we have inner fragment transactions
        if (currentFragmentBaseTag != null) {
            contentFragment = (BaseFragment) fragmentManager.findFragmentByTag(currentFragmentBaseTag);
        } else {
            contentFragment = null;
        }
    }

    private void refreshDrawerIndicator() {
        if (drawerToggle != null) {
            drawerToggle.setDrawerIndicatorEnabled(!(fragmentManager.getBackStackEntryCount() > 1));
        }
    }

    public void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    //================================================================================
    // Toolbar
    //================================================================================

    public void setToolbar(Toolbar toolbar) {
        setToolbar(toolbar, true);
    }

    public void setToolbar(Toolbar toolbar, boolean setDisplayHomeAsUp) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(setDisplayHomeAsUp);
                getSupportActionBar().setHomeButtonEnabled(setDisplayHomeAsUp);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarEditIv.setVisibility(View.INVISIBLE);
                toolbarDeleteIv.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setToolbar(Toolbar toolbar, boolean setDisplayHomeAsUp, String title, boolean showEditAndDeleteIconInToolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(setDisplayHomeAsUp);
                getSupportActionBar().setHomeButtonEnabled(setDisplayHomeAsUp);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setWindowTitle(title);
                getSupportActionBar().setTitle(title);
                Log.d(LOG_TAG, "Edit And Delete Icon Shown in Toolbar: " + showEditAndDeleteIconInToolbar);
                toolbar.setTitleTextColor(Color.WHITE);
                if (contentFragment.isRootFragment()){  //setting Hamburger Icon instead of Arrow if Fragment is rootFragment
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
                }
            }
        }
    }

    private boolean isNavigationEnabled(BaseFragment fragment) {
//        return !((fragment instanceof LoginFragment) ||
//                (fragment instanceof WeeklyQuizRootFragment) ||
//                (fragment instanceof QuizKnowledgeFragment) ||
//                (fragment instanceof RegistrationAddressFragment) ||
//                (fragment instanceof Intro53Fragment) ||
//                (fragment instanceof Intro53CarPickerFragment) ||
//                (fragment instanceof Intro53ResidentialPickerFragment));
        return true;    //entfernen wenn die Methode gebraucht wird!
    }

}
