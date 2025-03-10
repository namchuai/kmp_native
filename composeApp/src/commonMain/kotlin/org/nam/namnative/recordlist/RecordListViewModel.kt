package org.nam.namnative.recordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import models.RecordInfo
import repositories.RecordInfoRepository

class RecordListViewModel(
    private val recordListRepository: RecordInfoRepository,
) : ViewModel() {

    private val _recordList = MutableStateFlow<List<RecordInfo>>(emptyList())
    val recordList = _recordList.asStateFlow()

    fun loadRecords() {
        viewModelScope.launch {
            _recordList.value = recordListRepository.getAllRecords()
        }
    }

    fun onDeleteClick(record: RecordInfo) {
        viewModelScope.launch {
            recordListRepository.deleteRecord(record.id)
            _recordList.value = recordListRepository.getAllRecords()
        }
    }
}