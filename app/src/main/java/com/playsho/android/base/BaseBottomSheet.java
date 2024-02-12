package com.playsho.android.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.playsho.android.R;
import com.playsho.android.db.SessionStorage;
import com.playsho.android.utils.Validator;

public abstract class BaseBottomSheet<B extends ViewDataBinding> extends BottomSheetDialogFragment {


    protected B binding;
    private String origin;
    protected BottomSheetStatusCallback bottomSheetStatusCallback;
    protected BottomSheetResultCallback bottomSheetResultCallback;

    public String getClassName(){
        return this.getClass().getSimpleName();
    }

    public interface BottomSheetStatusCallback  {

        void onBottomSheetShow();

        void onBottomSheetDismiss();

    }

    public interface BottomSheetResultCallback   {

        void onBottomSheetProcessSuccess(String data);

        void onBottomSheetProcessFail(String data);
    }

    /**
     * Get the resource ID for the layout of the fragment.
     *
     * @return the layout resource ID
     */
    protected abstract int getLayoutResourceId();

    /**
     * Initialize views in the fragment.
     */
    protected abstract void initView();

    /**
     * Constructor for the BaseBottomSheet class.
     * It sets the origin of the fragment based on the parent fragment or the activity if there is no parent fragment.
     */
    public BaseBottomSheet() {
        Fragment parentFragment = getParentFragment();
        setOrigin( parentFragment != null ?
                parentFragment.getClass().getSimpleName() :
                (getActivity() != null ? getActivity().getClass().getSimpleName() : "")
        );
    }

    /**
     * Set the BottomSheetCallback for the fragment.
     *
     * @param bottomSheetCallback the BottomSheetCallback to set
     */
    public void setStatusCallback(BottomSheetStatusCallback bottomSheetCallback) {
        this.bottomSheetStatusCallback = bottomSheetCallback;
    }

    public void setResultCallback(BottomSheetResultCallback callback) {
        this.bottomSheetResultCallback = callback;
    }

    /**
     * Set the origin of the fragment.
     *
     * @param origin the origin of the fragment
     */
    protected void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Get the origin of the fragment.
     *
     * @return the origin of the fragment
     */
    protected String getOrigin() {
        return this.origin;
    }

    /**
     * Inflate the view for the fragment.
     *
     * @param inflater           the layout inflater
     * @param container          the view group container
     * @param savedInstanceState the saved instance state
     * @return the inflated view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), container, false);
        return binding.getRoot();
    }

    /**
     * Called when the view has been created.
     *
     * @param view               the created view
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    /**
     * Get the session storage instance.
     *
     * @return the session storage instance
     */
    protected SessionStorage getSessionStorage() {
        return ApplicationLoader.getSessionStorage();
    }

    /**
     * Show the fragment.
     */
    public void show() {
        show(getParentFragmentManager(), getOrigin());
    }

    /**
     * Get the custom theme for the bottom sheet dialog.
     *
     * @return the custom theme resource ID
     */
    @Override
    public int getTheme() {
        return R.style.Base_Theme_Playsho;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}