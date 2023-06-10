package net.critika.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TestHelpers {
    suspend fun createTestUser(email: String, password: String): UserRecord {
        return withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().createUser(
                UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(true),
            )
        }
    }

    suspend fun deleteTestUser(uid: String) {
        withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().deleteUser(uid)
        }
    }
}
