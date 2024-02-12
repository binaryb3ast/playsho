package com.playsho.android.component

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ActivityLauncher<Input, Result> private constructor(
    private val launcher: ActivityResultLauncher<Input>,
    private var onActivityResult: OnActivityResult<Result>?
) {
    interface OnActivityResult<O> {
        fun onActivityResult(result: O)
    }

    companion object {
        @NonNull
        @JvmStatic
        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>,
            onActivityResult: OnActivityResult<Result>?
        ): ActivityLauncher<Input, Result> {
            return ActivityLauncher(caller.registerForActivityResult(contract) {
                onActivityResult?.onActivityResult(it)
            }, onActivityResult)
        }

        @NonNull
        @JvmStatic
        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>
        ): ActivityLauncher<Input, Result> {
            return registerForActivityResult(caller, contract, null)
        }

        @NonNull
        @JvmStatic
        fun registerActivityForResult(caller: ActivityResultCaller): ActivityLauncher<Intent, ActivityResult> {
            return registerForActivityResult(
                caller,
                ActivityResultContracts.StartActivityForResult()
            )
        }
    }

    fun setOnActivityResult(onActivityResult: OnActivityResult<Result>?) {
        this.onActivityResult = onActivityResult
    }

    fun launch(input: Input, onActivityResult: OnActivityResult<Result>?) {
        this.onActivityResult = onActivityResult
        launcher.launch(input)
    }

    fun launch(input: Input) {
        launch(input, onActivityResult)
    }

    private fun callOnActivityResult(result: Result) {
        onActivityResult?.onActivityResult(result)
    }
}
