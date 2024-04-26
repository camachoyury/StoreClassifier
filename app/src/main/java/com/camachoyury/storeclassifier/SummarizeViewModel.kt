package com.camachoyury.storeclassifier

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummarizeViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<SummarizeUiState> =
        MutableStateFlow(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> =
        _uiState.asStateFlow()

    fun summarize(inputText: String, bitmap: Bitmap? = null){
        _uiState.value = SummarizeUiState.Loading

        val prompt = inputText

        viewModelScope.launch {

            val inputContent = content {
                if (bitmap != null) {
                    image(bitmap)
                }
                text(prompt)
            }

            try {

                val response = generativeModel.generateContent(inputContent)

//                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    println(outputContent)
                    _uiState.value = SummarizeUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}