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
                ?: "anglepro-48192-firebase-adminsdk-aq2y7-805cfa99f3.json"

            println("Using credentials file: $credentials")
            println("Absolute path: ${File(credentials).absolutePath}")
            println("Working directory: ${System.getProperty("user.dir")}")
            println("File exists: ${File(credentials).exists()}")

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