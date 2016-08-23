package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.os.Bundle;

import com.examples.pj.einkaufsapp.R;

/**
 * Class as Container for About Information
 */
public class AboutFragment extends BaseFragment {
    public static final String LOG_TAG = AboutFragment.class.getSimpleName();

    private Context context;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;
    private String TOOLBAR_TITLE;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public AboutFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance as container for bundle arguments
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new AboutFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        if (toolbarTv != null) {
            toolbarTv.setText(TOOLBAR_TITLE);
        }
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    protected void onCleanUp() {
        // not needed
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onResume() {
        super.onResume();

        context = this.getActivity();
        TOOLBAR_TITLE = context.getResources().getString(R.string.toolbar_title_about);
        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
    }
}