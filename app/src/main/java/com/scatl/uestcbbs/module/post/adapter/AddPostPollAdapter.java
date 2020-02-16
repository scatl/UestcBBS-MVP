package com.scatl.uestcbbs.module.post.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/16 14:09
 */
public class AddPostPollAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AddPostPollAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.item_add_poll_edittext, item)
                .setText(R.id.item_add_poll_count, String.valueOf(helper.getLayoutPosition() + 1 + ". "))
                .addOnClickListener(R.id.item_add_poll_delete);

        ImageView delete = helper.getView(R.id.item_add_poll_delete);
        if (helper.getLayoutPosition() == 0 || helper.getLayoutPosition() == 1) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }

        ((EditText)helper.getView(R.id.item_add_poll_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                getData().set(helper.getLayoutPosition(), s.toString());
            }
        });
    }
}
