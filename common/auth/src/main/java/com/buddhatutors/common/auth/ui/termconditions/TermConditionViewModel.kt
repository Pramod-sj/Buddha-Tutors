package com.buddhatutors.common.auth.ui.termconditions

import androidx.lifecycle.ViewModel
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.TermCondition
import com.buddhatutors.domain.usecase.remoteconfig.GetTermsAndConditions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TermConditionViewModel @Inject constructor(
    private val getTermCondition: GetTermsAndConditions
) : ViewModel() {

    private val _termConditions = MutableStateFlow<List<TermCondition>>(emptyList())
    val termCondition: StateFlow<List<TermCondition>> get() = _termConditions

    fun loadTermConditions() {
        when (val resource = getTermCondition()) {
            is Resource.Error -> {
                //handle error state for empty terms and conditions
            }

            is Resource.Success -> {
                _termConditions.value = resource.data
            }
        }
    }

}