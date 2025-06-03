package com.example.cashcowsaver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

    @Parcelize
    data class Transaction(
        val category: String,
        val description: String,
        val amount: Double,
        val date: String,
        val type: String,//income or expense
        val iconResId: Int//drawable resources ID for category icon//
    ) : Parcelable
