package com.buddhatutors.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}


/**
 * Observes a state in SavedStateHandle as a flow and resets it after handling.
 *
 * @param key The key for the state to observe.
 * @param defaultValue The default value for the state.
 * @param onEffect A lambda to handle the state effect.
 */
@Composable
fun <T> SavedStateHandle.collectAndResetState(
    key: String,
    defaultValue: T,
    onEffect: (T) -> Unit
) {
    LaunchedEffect(Unit) {
        getStateFlow(key, defaultValue) // Optional: Emit only when value changes
            .collect { value ->
                if (value != defaultValue) {
                    onEffect(value)
                    // Reset to the default value
                    set(key, defaultValue)
                }
            }
    }
}