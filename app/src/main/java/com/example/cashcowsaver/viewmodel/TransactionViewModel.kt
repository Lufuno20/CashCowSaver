package com.example.cashcowsaver.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.database.TransactionDao
import com.example.cashcowsaver.models.Transaction
import com.example.cashcowsaver.models.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/*
enum class TransactionFilter { ALL, INCOME, EXPENSE }

class TransactionViewModel(private val dao: TransactionDao) : ViewModel() {

    private val filterType = MutableStateFlow(TransactionFilter.ALL)

    val transactions: StateFlow<List<TransactionEntity>> = filterType
        .flatMapLatest { filter ->
            when (filter) {
                TransactionFilter.ALL -> dao.getAllTransactions()
                TransactionFilter.INCOME -> dao.getIncomeTransactions()
                TransactionFilter.EXPENSE -> dao.getExpenseTransactions()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setFilter(filter: TransactionFilter) {
        filterType.value = filter
    }
}
*/