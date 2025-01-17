/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
fun DownloadLanguageDialog(
    downloadDialogData: List<UiDownloadData>,
    onDownloadRequest: (List<UiDownloadData>) -> Unit,
    downloadProgress: Float,
    dataRemaining: String,
    onNoConnection: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var downloadStarted by rememberSaveable(downloadDialogData) {
        mutableStateOf(false)
    }

    if (!downloadStarted) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            icon = { Icon(Icons.Outlined.Download, null) },
            title = { Text(stringResource(id = R.string.no_data)) },
            text = {
                Text(
                    stringResource(
                        id = R.string.download_description,
                        downloadDialogData.firstOrNull()?.type?.displayName ?: "",
                        downloadDialogData.joinToString(separator = ", ") { it.name }
                    )
                )
            },
            onDismissRequest = {},
            confirmButton = {
                EnhancedButton(
                    onClick = {
                        if (context.isNetworkAvailable()) {
                            downloadDialogData.let { downloadData ->
                                onDownloadRequest(downloadData)
                                downloadStarted = true
                            }
                        } else onNoConnection()
                    }
                ) {
                    Text(stringResource(R.string.download))
                }
            },
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    } else {
        BasicAlertDialog(onDismissRequest = {}) {
            Box(
                Modifier.fillMaxSize()
            ) {
                Loading(downloadProgress / 100) {
                    AutoSizeText(
                        text = dataRemaining,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(it * 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION")
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}