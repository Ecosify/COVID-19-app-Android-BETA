/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.interstitials

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_workplace_guidance.workplace_latest_advice
import kotlinx.android.synthetic.main.white_banner.toolbar
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.util.WORKPLACE_GUIDANCE
import effiia.com.Safe2.sonar.android.app.util.openUrl
import effiia.com.Safe2.sonar.android.app.util.setNavigateUpToolbar

class WorkplaceGuidanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workplace_guidance)

        setNavigateUpToolbar(toolbar, title = R.string.guidance_for_workplace)

        workplace_latest_advice.setOnClickListener {
            openUrl(WORKPLACE_GUIDANCE)
        }
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(getIntent(context))

        private fun getIntent(context: Context) =
            Intent(context, WorkplaceGuidanceActivity::class.java)
    }
}
