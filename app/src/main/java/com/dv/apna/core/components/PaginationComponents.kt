package com.dv.apna.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dv.apna.core.utils.sdp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

const val DEFAULT_PAGE_SIZE = 15
const val PAGINATION_LOADER_KEY = "pagination_load_more"

/**
 * Holds the paginated slice, scroll state, and callbacks for scroll-to-load-more lists.
 */
data class PaginationState<T>(
    val visibleItems: List<T>,
    val listState: LazyListState,
    val totalCount: Int,
    val nextBatchCount: Int,
    val hasMore: Boolean,
    val loadMore: () -> Unit,
    val reset: () -> Unit
)

/**
 * Remembers incremental page state over [items] and only loads the next batch once the
 * bottom loader item actually becomes visible in [listState].
 */
@Composable
fun <T> rememberPaginationState(
    items: List<T>,
    pageSize: Int = DEFAULT_PAGE_SIZE
): PaginationState<T> {
    var visibleCount by rememberSaveable { mutableIntStateOf(pageSize) }
    var resetCounter by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    val clampedCount = visibleCount.coerceAtMost(items.size)
    val visibleItems = remember(items, clampedCount) { items.take(clampedCount) }
    val remaining = (items.size - clampedCount).coerceAtLeast(0)
    val nextBatchCount = remaining.coerceAtMost(pageSize)
    val hasMore = remaining > 0

    LaunchedEffect(resetCounter) {
        if (resetCounter > 0) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(listState, items.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            val loaderVisible = info.visibleItemsInfo.any { it.key == PAGINATION_LOADER_KEY }
            loaderVisible to info.totalItemsCount
        }
            .distinctUntilChanged()
            .filter { (loaderVisible, _) -> loaderVisible }
            .collectLatest {
                delay(450)
                if (visibleCount < items.size) {
                    visibleCount += pageSize
                }
            }
    }

    return PaginationState(
        visibleItems = visibleItems,
        listState = listState,
        totalCount = items.size,
        nextBatchCount = nextBatchCount,
        hasMore = hasMore,
        loadMore = { visibleCount += pageSize },
        reset = {
            visibleCount = pageSize
            resetCounter += 1
        }
    )
}

/**
 * Compact bottom spinner shown while scrolling loads the next batch.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun PaginationLoadMoreFooter(
    shownCount: Int,
    totalCount: Int,
    nextBatchCount: Int,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.sdp()),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.sdp()),
            color = Color(0xFF2CA074),
            strokeWidth = 2.5.dp
        )
    }
}
