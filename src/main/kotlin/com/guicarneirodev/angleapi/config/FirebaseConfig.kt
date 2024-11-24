package com.guicarneirodev.angleapi.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

object FirebaseConfig {
    fun initialize() {
        try {
            val credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
                ?: "firebase-credentials.json"

            println("Loading Firebase credentials from: $credentials")
            println("Working directory: ${System.getProperty("user.dir")}")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(FileInputStream(credentials)))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                println("Firebase initialized successfully")
            }
        } catch (e: Exception) {
            println("Error initializing Firebase: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}