package com.examples.pj.einkaufsapp.fragments;

import android.os.Bundle;

import com.examples.pj.einkaufsapp.R;

/**
 * Fore already done shopping events
 */
public class HistoricListsFragment extends BaseFragment {
    public static final String LOG_TAG = HistoricListsFragment.class.getSimpleName();

    private static final String TOOLBAR_TITLE = "Erledigte Einkäufe";
    private boolean showEditAndDeleteIconInToolbar;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public HistoricListsFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance for getting arguments in bundle
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new HistoricListsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_historic_lists;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarTv.setText(TOOLBAR_TITLE);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onResume() {
        super.onResume();

        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
    }
}