package com.dating.home.presentation.chat.gif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.util.Result
import com.dating.home.domain.giphy.GiphyGif
import com.dating.home.domain.giphy.GiphyService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun GifPickerSheet(
    giphyService: GiphyService,
    onGifSelected: (GiphyGif) -> Unit,
    modifier: Modifier = Modifier
) {
    var gifs by remember { mutableStateOf<List<GiphyGif>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val searchState = rememberTextFieldState()

    // Load trending on first launch
    LaunchedEffect(Unit) {
        when (val result = giphyService.trending()) {
            is Result.Success -> gifs = result.data
            is Result.Failure -> Unit
        }
        isLoading = false
    }

    // Search with debounce
    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .debounce(400)
            .distinctUntilChanged()
            .collect { query ->
                isLoading = true
                if (query.isBlank()) {
                    when (val result = giphyService.trending()) {
                        is Result.Success -> gifs = result.data
                        is Result.Failure -> Unit
                    }
                } else {
                    when (val result = giphyService.search(query)) {
                        is Result.Success -> gifs = result.data
                        is Result.Failure -> Unit
                    }
                }
                isLoading = false
            }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        // Search bar
        SearchBar(
            state = searchState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // GIF grid
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (gifs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No GIFs found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalItemSpacing = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(gifs, key = { it.id }) { gif ->
                    val ratio = if (gif.height > 0) {
                        gif.width.toFloat() / gif.height.toFloat()
                    } else 1f

                    AsyncImage(
                        model = gif.previewUrl,
                        contentDescription = "GIF",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio.coerceIn(0.5f, 2f))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onGifSelected(gif) }
                    )
                }
            }
        }

        // Giphy attribution
        Text(
            text = "Powered by GIPHY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun SearchBar(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        state = state,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.extended.textPrimary
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorator = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.extended.surfaceHigher,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = "Search GIFs",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        },
        modifier = modifier
    )
}
