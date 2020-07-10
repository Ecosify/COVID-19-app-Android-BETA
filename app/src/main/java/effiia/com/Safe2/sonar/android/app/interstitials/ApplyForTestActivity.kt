/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.interstitials

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_apply_for_test.order_clinical_tests
import kotlinx.android.synthetic.main.activity_reference_code.reference_code_panel
import kotlinx.android.synthetic.main.white_banner.toolbar
import effiia.com.Safe2.sonar.android.app.BuildConfig
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.appComponent
import effiia.com.Safe2.sonar.android.app.common.BaseActivity
import effiia.com.Safe2.sonar.android.app.common.ViewModelFactory
import effiia.com.Safe2.sonar.android.app.referencecode.ReferenceCode
import effiia.com.Safe2.sonar.android.app.referencecode.ReferenceCodeViewModel
import effiia.com.Safe2.sonar.android.app.referencecode.ReferenceCodeViewModel.State.Loaded
import effiia.com.Safe2.sonar.android.app.util.openUrl
import effiia.com.Safe2.sonar.android.app.util.setNavigateUpToolbar
import javax.inject.Inject

class ApplyForTestActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelFactory<ReferenceCodeViewModel>

    private val viewModel: ReferenceCodeViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        setContentView(R.layout.activity_apply_for_test)
        setNavigateUpToolbar(toolbar, title = R.string.apply_for_test_title)

        viewModel.state().observe(this, Observer { state ->
            reference_code_panel.setState(state)

            order_clinical_tests.setOnClickListener {
                val url = when (state) {
                    is Loaded -> buildUrlWithCode(state.code)
                    else -> buildUrlWithoutCode()
                }
                openUrl(
                    url = url,
                    useInternalBrowser = false
                )
            }
        })

        viewModel.getReferenceCode()
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(getIntent(context))

        private fun getIntent(context: Context) =
            Intent(context, ApplyForTestActivity::class.java)

        private fun buildUrlWithCode(code: ReferenceCode): String =
            "${BuildConfig.URL_APPLY_CORONAVIRUS_TEST}?ctaToken=${code.value}"

        private fun buildUrlWithoutCode(): String =
            BuildConfig.URL_APPLY_CORONAVIRUS_TEST
    }
}
