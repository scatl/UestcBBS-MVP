package com.scatl.widget.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * created by sca_tl at 2022/12/30 19:27
 */
public class ViewPagerBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ViewPagerBottomSheetDialog(getContext(), getTheme());
    }

}
