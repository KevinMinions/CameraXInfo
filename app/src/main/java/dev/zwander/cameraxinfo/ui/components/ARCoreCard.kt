package dev.zwander.cameraxinfo.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import dev.zwander.cameraxinfo.R
import kotlinx.coroutines.delay

@Composable
fun ARCoreCard() {
    val context = LocalContext.current.applicationContext
    var arCoreStatus by remember {
        mutableStateOf<ArCoreApk.Availability?>(null)
    }
    var depthStatus by remember {
        mutableStateOf<Boolean?>(null)
    }

    LaunchedEffect(key1 = null) {
        val status = ArCoreApk.getInstance().checkAvailability(context).await(context)

        depthStatus = if (status == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
            val session = Session(context)
            session.isDepthModeSupported(Config.DepthMode.AUTOMATIC).also {
                session.close()
            }
        } else {
            null
        }

        arCoreStatus = status
    }

    PaddedColumnCard {
        Text(
            text = stringResource(id = R.string.ar_core),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Divider(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 16.dp)
                .fillMaxWidth(0.33f)
        )

        Text(
            text = stringResource(id = R.string.ar_core),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        FlowRow(
            mainAxisSize = SizeMode.Expand,
            mainAxisAlignment = MainAxisAlignment.SpaceEvenly,
            mainAxisSpacing = 8.dp
        ) {
            val (arCoreText, arCoreColor) = when {
                arCoreStatus?.isSupported == true -> R.string.supported to Color.Green
                arCoreStatus == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> R.string.unsupported to Color.Red
                else -> R.string.unknown to Color.Yellow
            }

            Text(
                text = stringResource(id = arCoreText),
                color = arCoreColor
            )

            val (arCoreInstallText, arCoreInstallColor) = when (arCoreStatus) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> R.string.installed to Color.Green
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> R.string.outdated to Color.Yellow
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> R.string.not_installed to Color.Red
                else -> R.string.unknown to Color.Yellow
            }

            Text(
                text = stringResource(id = arCoreInstallText),
                color = arCoreInstallColor
            )
        }

        Spacer(Modifier.size(4.dp))

        Text(
            text = stringResource(id = R.string.depth_api),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        FlowRow(
            mainAxisSize = SizeMode.Expand,
            mainAxisAlignment = MainAxisAlignment.SpaceEvenly,
            mainAxisSpacing = 8.dp
        ) {
            val (depthText, depthColor) = when (depthStatus) {
                true -> R.string.supported to Color.Green
                false -> R.string.unsupported to Color.Red
                else -> R.string.unknown to Color.Yellow
            }

            Text(
                stringResource(id = depthText),
                color = depthColor
            )
        }
    }
}

private suspend fun ArCoreApk.Availability.await(context: Context): ArCoreApk.Availability {
    return if (isTransient) {
        delay(200)
        ArCoreApk.getInstance().checkAvailability(context).await(context)
    } else {
        this
    }
}