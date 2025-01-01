package com.guicarneirodev.angleapi.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream

object FirebaseConfig {
    fun initialize() {
        try {
            val credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
                ?: "firebase-credentials.json"

            println("Loading credentials file: $credentials")
            val file = File(credentials)
            println("File exists: ${file.exists()}")
            println("File readable: ${file.canRead()}")
            println("File size: ${file.length()}")

            val stream = FileInputStream(credentials)
            println("Stream available: ${stream.available()}")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
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