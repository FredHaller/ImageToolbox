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

package ru.tech.imageresizershrinker.feature.limits_resize.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.fileSize
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.components.AutoRotateLimitBoxToggle
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.components.LimitsResizeSelector
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.viewModel.LimitsResizeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitsResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: LimitsResizeViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            viewModel.decodeBitmapFromUri(
                uri = uris[0],
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                viewModel.decodeBitmapFromUri(
                    uri = uris[0],
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }

    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { failed, savingPath ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = savingPath,
                showConfetti = showConfetti
            )
        }
    }

    val focus = LocalFocusManager.current
    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() }
    )

    LaunchedEffect(imageState) {
        if (imageState.isExpanded()) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    val imageBlock = @Composable {
        ImageContainer(
            imageInside = imageInside,
            showOriginal = false,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isImageLoading,
            shouldShowPreview = true
        )
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val actions: @Composable RowScope.() -> Unit = {
        if (viewModel.previewBitmap != null) {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.shareBitmaps { showConfetti() }
                },
                enabled = viewModel.canSave
            ) {
                Icon(Icons.Outlined.Share, null)
            }
        }
        ZoomButton(
            onClick = { showZoomSheet.value = true },
            visible = viewModel.bitmap != null,
        )
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            isPrimaryButtonVisible = viewModel.canSave,
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onPrimaryButtonClick = saveBitmaps,
            actions = {
                if (imageInside) actions()
            }
        )
    }

    ZoomModalSheet(
        data = viewModel.previewBitmap,
        visible = showZoomSheet
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        TopAppBarTitle(
                            title = stringResource(R.string.limits_resize),
                            input = viewModel.bitmap,
                            isLoading = viewModel.isImageLoading,
                            size = viewModel.selectedUri?.fileSize(LocalContext.current) ?: 0L
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
                        if (!imageInside) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .container(
                                    shape = RectangleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                )
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                    }

                    val internalHeight = rememberAvailableHeight(imageState = imageState)
                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 100.dp),
                            top = if (viewModel.bitmap == null || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside && viewModel.bitmap != null,
                            imageState = imageState,
                            onStateChange = { imageState = it },
                            internalHeight = internalHeight,
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.bitmap == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (imageInside && viewModel.bitmap == null) imageBlock()
                                if (viewModel.bitmap != null) {
                                    ImageCounter(
                                        imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                                        onRepick = {
                                            showPickImageFromUrisSheet.value = true
                                        }
                                    )
                                    ResizeImageField(
                                        imageInfo = viewModel.imageInfo,
                                        originalSize = viewModel.originalSize,
                                        onWidthChange = viewModel::updateWidth,
                                        onHeightChange = viewModel::updateHeight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        imageFormat = viewModel.imageInfo.imageFormat,
                                        checked = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                                        Modifier.size(8.dp)
                                    )
                                    QualityWidget(
                                        imageFormat = viewModel.imageInfo.imageFormat,
                                        enabled = viewModel.bitmap != null,
                                        quality = viewModel.imageInfo.quality,
                                        onQualityChange = viewModel::setQuality
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        value = viewModel.imageInfo.imageFormat,
                                        onValueChange = viewModel::setMime
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    AutoRotateLimitBoxToggle(
                                        value = viewModel.resizeType.autoRotateLimitBox,
                                        onClick = viewModel::toggleAutoRotateLimitBox
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    LimitsResizeSelector(
                                        enabled = viewModel.bitmap != null,
                                        value = viewModel.resizeType,
                                        onValueChange = viewModel::setResizeType
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    ScaleModeSelector(
                                        value = viewModel.imageInfo.imageScaleMode,
                                        onValueChange = viewModel::setImageScaleMode
                                    )
                                } else if (!viewModel.isImageLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.size(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    if (!imageInside && viewModel.bitmap != null) {
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.bitmap == null) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            if (viewModel.isSaving) {
                LoadingDialog(
                    done = viewModel.done,
                    left = viewModel.uris?.size ?: 1
                ) {
                    viewModel.cancelSaving()
                }
            }

            PickImageFromUrisSheet(
                transformations = listOf(
                    viewModel.imageInfoTransformationFactory(
                        imageInfo = ImageInfo()
                    )
                ),
                visible = showPickImageFromUrisSheet,
                uris = viewModel.uris,
                selectedUri = viewModel.selectedUri,
                onUriPicked = { uri ->
                    try {
                        viewModel.setBitmap(uri = uri)
                    } catch (e: Exception) {
                        scope.launch {
                            toastHostState.showError(context, e)
                        }
                    }
                },
                onUriRemoved = { uri ->
                    viewModel.updateUrisSilently(removedUri = uri)
                },
                columns = if (imageInside) 2 else 4,
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler(onBack = onBack)
        }
    }
}