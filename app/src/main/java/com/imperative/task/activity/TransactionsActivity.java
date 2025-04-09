package com.imperative.task.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.imperative.task.R;
import com.imperative.task.adapter.TransactionAdapter;
import com.imperative.task.data.response.Transaction;
import com.imperative.task.retrofit.ApiService;
import com.imperative.task.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionsActivity extends AppCompatActivity {
    private RecyclerView transactionsListView;
    private Button logoutButton;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "auth_token";
    private AppDatabase database;
    private TransactionAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        transactionsListView = findViewById(R.id.transactions_list);
        logoutButton = findViewById(R.id.logout_button);
        searchView = findViewById(R.id.search_view);
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "transaction-database")
                .build();
        adapter = new TransactionAdapter();
        transactionsListView.setAdapter(adapter);

        setupSearchView();
        loadTransactions();

        logoutButton.setOnClickListener(v -> logout());
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void fetchTransactionsFromApi() {
        String token = getToken();
        if (token == null) {
            Toast.makeText(this, "No token found, please log in again", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        Log.e("TAG", "fetchTransactions:" + token);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.prepstripe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String authHeader = "Bearer " + token;
        apiService.getTransactions(authHeader).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("TAG", "onResponse: " + response.body());
                    ArrayList<Transaction> transactions = new ArrayList<>(response.body());
                    adapter.setTransactions(transactions);

                    new Thread(() -> {
                        database.transactionDao().deleteAll();
                        database.transactionDao().insertAll(transactions);
                    }).start();
                } else {
                    Toast.makeText(TransactionsActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Log.e("TAG", "onResponse: " + t.getMessage());
                Toast.makeText(TransactionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadTransactions() {
        if (isOnline()) {
            fetchTransactionsFromApi();
        } else {
            loadTransactionsFromDatabase();
        }
    }

    private void loadTransactionsFromDatabase() {
        new Thread(() -> {
            List<Transaction> transactions = database.transactionDao().getAllTransactions();
            runOnUiThread(() -> adapter.setTransactions(transactions));
        }).start();
    }

    private void logout() {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            EncryptedSharedPreferences prefs = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            prefs.edit().remove(TOKEN_KEY).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getToken() {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            EncryptedSharedPreferences prefs = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            return prefs.getString(TOKEN_KEY, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}