package com.imperative.task.biometric;

import android.content.Context;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.imperative.task.MainActivity;

public class BiometricHelper {
    private Context context;
    private Runnable onSuccess;

    public BiometricHelper(Context context, Runnable onSuccess) {
        this.context = context;
        this.onSuccess = onSuccess;
    }

    public void authenticate() {
        BiometricManager biometricManager = BiometricManager.from(context);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build();

            BiometricPrompt biometricPrompt = new BiometricPrompt((MainActivity) context,
                    ContextCompat.getMainExecutor(context), new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onSuccess.run();
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(context, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(context, "Biometric authentication not available", Toast.LENGTH_SHORT).show();
        }
    }
}