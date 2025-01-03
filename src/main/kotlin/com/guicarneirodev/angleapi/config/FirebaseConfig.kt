package com.guicarneirodev.angleapi.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream

object FirebaseConfig {
    fun initialize() {
        try {
            val credentialsPath = "firebase-credentials.json"
            val file = File(credentialsPath)

            require(file.exists()) { "Credentials file not found at: ${file.absolutePath}" }

            val credentials = GoogleCredentials
                .fromStream(FileInputStream(file))
                .createScoped(listOf(
                    "https://www.googleapis.com/auth/cloud-platform",
                    "https://www.googleapis.com/auth/datastore"
                ))

            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId("anglepro-48192")
                .build()

            FirebaseApp.initializeApp(options)
            println("Firebase initialized with project: anglepro-48192")
        } catch (e: Exception) {
            throw RuntimeException("Firebase initialization failed: ${e.message}", e)
        }
    }
}