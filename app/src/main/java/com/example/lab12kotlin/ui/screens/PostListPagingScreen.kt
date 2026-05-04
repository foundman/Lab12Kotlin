package com.example.lab12kotlin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.lab12kotlin.data.PostPagingProvider
import com.example.lab12kotlin.data.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListPagingScreen() {
    // Получаем поток PagingData и преобразуем в LazyPagingItems
    val posts = PostPagingProvider.getPostsFlow()
        .collectAsLazyPagingItems()

    // Основной экран с TopBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posts (Paging)", fontSize = 20.sp) }
            )
        }
    ) { padding ->
        // Обработка состояний загрузки (refresh)
        when (val refresh = posts.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = refresh.error.message ?: "Ошибка загрузки",
                        onRetry = { posts.retry() }
                    )
                }
            }
            is LoadState.NotLoading -> {
                // Список отображается ниже
            }
        }

        // Список постов
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Элементы списка
            items(
                count = posts.itemCount,
                key = { index -> posts[index]?.id ?: index }
            ) { index ->
                val post = posts[index]
                if (post != null) {
                    PostItem(post = post)
                } else {
                    // Элемент ещё не загружен - показываем заглушку
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
            }

            // Индикатор загрузки в конце списка (append)
            when (val append = posts.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            message = append.error.message ?: "Ошибка подгрузки",
                            onRetry = { posts.retry() }
                        )
                    }
                }
                else -> {}
            }
        }

        // Pull-to-refresh индикатор
        if (posts.loadState.refresh is LoadState.Loading && posts.itemCount > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/**
 * Вспомогательный компонент для отображения ошибки и кнопки повтора
 */
@Composable
private fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️ $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("🔄 Повторить")
        }
    }
}