package com.examples.pj.einkaufsapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.BaseActivity;
import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.util.NavigationManager;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Base Fragment, inherited by all other Fragments
 */
public abstract class BaseFragment extends Fragment {
    protected final String baseTag;

    private boolean isRootFragment;
    @Nullable
    @Bind(R.id.my_toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbarTv)
    TextView toolbarTv;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     *
     * @param tag String TAG name of inheriting Fragment
     */
    public BaseFragment(String tag) {
        this(tag, false);
    }

    /**
     * Constructor
     *
     * @param tag            String TAG name of inheriting Fragment
     * @param isRootFragment is Fragment a Root Fragment of navigation
     */
    public BaseFragment(String tag, boolean isRootFragment) {
        this.baseTag = tag;
        this.isRootFragment = isRootFragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    protected abstract int getLayoutId();

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //================================================================================
    // Other Methods
    //================================================================================

    protected final void finish() {
        NavigationManager.goBackFrom(this);
    }

    protected void setToolbar() {
        ((BaseActivity) getActivity()).setToolbar(toolbar);
    }

    protected void setToolbarEditAndDeleteIcon(boolean isVisible) {
    }

    /** indicator for possibility to move one step back or not
     *
     * @return true or false
     */
    public boolean canGoBack() {
        return true;
    }

    protected final void snack(int stringResId) {
        ViewUtils.makeSnackbar(getView(), stringResId).show();
    }

    protected final void snack(String text) {
        ViewUtils.makeSnackbar(getView(), text).show();
    }

    protected final void toast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    protected final void toast(int stringResId) {
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_SHORT).show();
    }

    public void setCustomAnimation(FragmentTransaction tx) {
    }

    public BaseActivity getAttachedActivity() {
        return (BaseActivity) getActivity();
    }

    public boolean isRootFragment() {
        return isRootFragment;
    }

    public String getBaseTag() {
        return baseTag;
    }
}
