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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@Composable
fun BottomButtonsBlock(
    targetState: Pair<Boolean, Boolean>,
    onPickImage: () -> Unit,
    onPrimaryButtonClick: () -> Unit,
    primaryButtonIcon: ImageVector = Icons.Rounded.Save,
    isPrimaryButtonVisible: Boolean = true,
    columnarFab: (@Composable ColumnScope.() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState
    ) { (isNull, inside) ->
        if (isNull) {
            EnhancedFloatingActionButton(
                onClick = onPickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp),
                content = {
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.pick_image_alt))
                    Spacer(Modifier.width(16.dp))
                }
            )
        } else if (inside) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = actions,
                floatingActionButton = {
                    Row {
                        EnhancedFloatingActionButton(
                            onClick = onPickImage,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(visible = isPrimaryButtonVisible) {
                            Row {
                                Spacer(Modifier.width(8.dp))
                                EnhancedFloatingActionButton(
                                    onClick = onPrimaryButtonClick
                                ) {
                                    Icon(
                                        imageVector = primaryButtonIcon,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            )
        } else {
            val direction = LocalLayoutDirection.current
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .container(
                        shape = RectangleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(
                        end = WindowInsets.displayCutout
                            .asPaddingValues()
                            .calculateEndPadding(direction)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row { actions() }
                Spacer(Modifier.height(8.dp))
                EnhancedFloatingActionButton(
                    onClick = onPickImage,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(imageVector = Icons.Rounded.AddPhotoAlternate, contentDescription = null)
                }
                columnarFab?.let {
                    Spacer(Modifier.height(8.dp))
                    it()
                }
                AnimatedVisibility(visible = isPrimaryButtonVisible) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        EnhancedFloatingActionButton(
                            onClick = onPrimaryButtonClick
                        ) {
                            Icon(
                                imageVector = primaryButtonIcon,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}