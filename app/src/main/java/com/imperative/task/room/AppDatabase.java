package com.imperative.task.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.imperative.task.data.response.Transaction;

@Database(entities = {Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
}
