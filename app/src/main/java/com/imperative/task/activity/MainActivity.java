package com.imperative.task.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.imperative.task.R;
import com.imperative.task.biometric.BiometricHelper;
import com.imperative.task.data.request.LoginRequest;
import com.imperative.task.data.response.LoginResponse;
import com.imperative.task.retrofit.ApiService;
import com.imperative.task.room.AppDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "auth_token";

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "transaction-database")
                .build();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        checkTokenAndAuthenticate();

        loginButton.setOnClickListener(v -> login());
    }

    private void checkTokenAndAuthenticate() {
        String token = getToken();
        if (token != null && !token.isEmpty()) {
            authenticateWithBiometrics();
        }
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.prepstripe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest request = new LoginRequest(username, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    saveToken(token);
                    authenticateWithBiometrics();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("TAG", "onFailure:" + "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateWithBiometrics() {
        BiometricHelper biometricHelper = new BiometricHelper(this, () -> {
            startActivity(new Intent(MainActivity.this, TransactionsActivity.class));
            finish();
        });
        biometricHelper.authenticate();
    }

    private void saveToken(String token) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            EncryptedSharedPreferences prefs = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            prefs.edit().putString(TOKEN_KEY, token).apply();
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