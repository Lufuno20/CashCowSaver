package com.example.cashcowsaver.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.models.Transaction
import kotlinx.coroutines.launch




    class TransactionViewModel(application: Application) : AndroidViewModel(application) {
        private val dao = AppDatabase.getDatabase(application).transactionDao()
        val allTransactions: LiveData<List<Transaction>> = dao.getAllTransactions()

        fun insertTransaction(transaction: Transaction) {
            viewModelScope.launch {
                dao.insert(transaction)
            }
        }
    }

