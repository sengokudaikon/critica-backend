package net.critika.e2e.user

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.infrastructure.authentication.FirebaseAdmin
import net.critika.main
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class AuthControllerTest : FunSpec() {
    private val mockFirebaseToken: FirebaseToken = mock(FirebaseToken::class.java)
    private val mockFirebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java)
    private val mockFirebaseApp: FirebaseApp = mock(FirebaseApp::class.java)
    override suspend fun beforeAny(testCase: TestCase) {
        whenever(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
        whenever(FirebaseAdmin.init()).thenReturn(mockFirebaseApp)
        whenever(mockFirebaseToken.uid).thenReturn("testUid")
        whenever(mockFirebaseAuth.verifyIdToken(anyString(), anyBoolean())).thenReturn(mockFirebaseToken)
    }

    @Test
    fun testPostApiAuthRefresh() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/refresh").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAuthRegister() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/register").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAuthRegisterfirebase() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/register-firebase").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAuthSignin() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/signIn").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAuthSignout() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/signout").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAuthUserexists() = testApplication {
        application {
            main()
        }
        client.post("/api/auth/userExists").apply {
            TODO("Please write your test here")
        }
    }
}
