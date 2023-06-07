package net.critika.infrastructure.authentication

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object FirebaseAdmin {
    @OptIn(ExperimentalEncodingApi::class)
    fun init(): FirebaseApp {
        var serviceAccountStream: InputStream? =
            this::class.java.classLoader.getResourceAsStream("adminsdk.json")

        if (serviceAccountStream == null) {
            val serviceAccountContent = Base64.decode(System.getenv("FIREBASE_SERVICE_ACCOUNT"))
            if (serviceAccountContent != null) {
                serviceAccountStream = serviceAccountContent.inputStream()
            } else {
                throw Exception("FIREBASE_SERVICE_ACCOUNT is not set")
            }
        }

        return FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build(),
        )
    }

    suspend fun createUserWithEmailAndPassword(email: String, username: String, password: String): UserRecord {
        return withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().createUser(
                UserRecord
                    .CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false)
                    .setDisplayName(username)
                    .setDisabled(false),
            )
        }
    }
}
