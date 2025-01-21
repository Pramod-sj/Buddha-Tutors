package com.buddhatutors.core.auth.domain

data class EmailNotVerifiedException(override val message: String) : Throwable()
