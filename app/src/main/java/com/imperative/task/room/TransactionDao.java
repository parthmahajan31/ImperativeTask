package com.imperative.task.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.imperative.task.data.response.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insertAll(List<Transaction> transactions);

    @Query("SELECT * FROM transactions")
    List<Transaction> getAllTransactions();

    @Query("DELETE FROM transactions")
    void deleteAll();
}
