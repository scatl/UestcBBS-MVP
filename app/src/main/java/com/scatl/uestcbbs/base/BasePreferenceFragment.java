package com.scatl.uestcbbs.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

public abstract class BasePreferenceFragment<P extends BasePresenter> extends PreferenceFragmentCompat {

    protected Activity mActivity;

    public P presenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);
    }


    protected abstract P initPresenter();

    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void setAllPreferencesToAvoidHavingExtraSpace(Preference preference) {
        preference.setIconSpaceReserved(false);
        if (preference instanceof PreferenceGroup) {
            for (int i = 0; i < ((PreferenceGroup) preference).getPreferenceCount(); i ++) {
                setAllPreferencesToAvoidHavingExtraSpace(((PreferenceGroup) preference).getPreference(i));
            }
        }
    }

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null)
            setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen);
        super.setPreferenceScreen(preferenceScreen);
    }

    @Override
    @SuppressLint("RestrictedApi")
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        new PreferenceGroupAdapter(preferenceScreen) {
            @Override
            public void onPreferenceHierarchyChange(Preference preference) {
                if (preference != null)
                    setAllPreferencesToAvoidHavingExtraSpace(preference);
                super.onPreferenceHierarchyChange(preference);
            }
        };

        return super.onCreateAdapter(preferenceScreen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detachView();
    }
}
