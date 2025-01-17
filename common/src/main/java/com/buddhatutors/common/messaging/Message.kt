package com.buddhatutors.common.messaging

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.UUID


object MessageHelper {

    private val messageChannel = Channel<Message>()
    val message: Flow<Message>
        get() = messageChannel.receiveAsFlow()

    fun showMessage(message: Message) {
        messageChannel.trySend(message)
    }

}

sealed class Message {

    private val id = UUID.randomUUID()

    abstract val text: String

    data class Success(override val text: String) : Message()

    data class Warning(
        override val text: String,
        val actionLabel: String? = null,
        val actionCallback: (() -> Unit)? = null
    ) : Message()

}