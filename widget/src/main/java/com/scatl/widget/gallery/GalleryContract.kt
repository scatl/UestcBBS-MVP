package com.scatl.widget.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * Created by sca_tl at 2023/6/14 20:27
 */
class GalleryContract: ActivityResultContract<Gallery, List<MediaEntity>>() {

    companion object {
        const val KEY_REQUEST = "key_request"
        const val KEY_RESULT = "key_result"
    }

    override fun createIntent(context: Context, input: Gallery): Intent {
        return Intent(context, GalleryActivity::class.java).apply {
            putExtra(KEY_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<MediaEntity> {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            intent.getParcelableArrayListExtra(KEY_RESULT) ?: emptyList()
        } else {
            emptyList()
        }
    }

}