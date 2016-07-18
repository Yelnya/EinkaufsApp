package com.examples.pj.einkaufsapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.BaseActivity;
import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.util.NavigationManager;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

//MANDATORY CLASS #5
public abstract class BaseFragment extends Fragment {
    protected final String baseTag;

    private boolean isRootFragment;
    @Nullable
    @Bind(R.id.my_toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbarTv)
    TextView toolbarTv;
    @Bind(R.id.toolbarEditIv)
    ImageView toolbarEditIv;
    @Bind(R.id.toolbarDeleteIv)
    ImageView toolbarDeleteIv;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    public BaseFragment(String tag) {
        this(tag, false);
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //================================================================================
    // Other Methods
    //================================================================================

    public final void startActivity(Class<?> cls, boolean finish) {
        Intent i = new Intent(getActivity(), cls);
        startActivity(i);
        if (finish) {
            getActivity().finish();
        }
    }

    protected final void finish() {
        NavigationManager.goBackFrom(this);
    }

    protected void setToolbar() {
        ((BaseActivity) getActivity()).setToolbar(toolbar);
    }

    protected void setToolbarEditAndDeleteIcon(boolean isVisible) {
    }

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
