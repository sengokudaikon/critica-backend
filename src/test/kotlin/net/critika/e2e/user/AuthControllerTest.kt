package net.critika.e2e.user

import arrow.core.right
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.critika.application.user.response.UserResponse
import net.critika.application.user.usecase.AuthUseCase
import net.critika.fixtures.AuthFixtures.createCommand
import net.critika.fixtures.AuthFixtures.existsQuery
import net.critika.fixtures.AuthFixtures.signInCommand
import net.critika.helpers.Helpers.getMockUser
import net.critika.infrastructure.authentication.FirebaseAdmin
import net.critika.infrastructure.authentication.FirebasePrincipal
import net.critika.main
import net.critika.ports.user.AuthPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class AuthControllerTest : FunSpec() {
    private val mockFirebaseToken: FirebaseToken = mock(FirebaseToken::class.java)
    private val mockFirebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java)
    private val mockFirebaseApp: FirebaseApp = mock(FirebaseApp::class.java)
    private val mockPrincipal: FirebasePrincipal = mock<FirebasePrincipal>()
    private val mockAuthUseCase: AuthPort = mock<AuthUseCase>()
    override suspend fun beforeAny(testCase: TestCase) {
        whenever(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
        whenever(FirebaseAdmin.init()).thenReturn(mockFirebaseApp)
        whenever(mockFirebaseToken.uid).thenReturn("testUid")
        whenever(mockPrincipal.uid).thenReturn("testUid")
        whenever(mockFirebaseAuth.verifyIdToken(anyString(), anyBoolean())).thenReturn(mockFirebaseToken)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostApiAuthRegister() = testApplication {
        application {
            main()
        }
        val mockUser = getMockUser().right()
        whenever(mockAuthUseCase.register(anyString(), any())).thenReturn(mockUser)

        val command = createCommand()
        val call = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            body = command.toString()
            header("Authorization", "Bearer ".plus(mockFirebaseToken.toString()))
        }
        val response = Json.decodeFromString<Map<String, String>>(call.content.awaitContent().toString())
        assertEquals(HttpStatusCode.Created, call.status)
        assertEquals(mockFirebaseToken.uid, response["uid"])
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostApiAuthSignIn() = testApplication {
        application {
            main()
        }
        val mockUser = getMockUser().right()
        whenever(mockAuthUseCase.signIn(anyString(), any())).thenReturn(mockUser)

        val call = client.post("/api/auth/signIn") {
            contentType(ContentType.Application.Json)
            body = signInCommand().toString()
        }
        val response = Json.decodeFromString<UserResponse>(call.content.awaitContent().toString())
        assertEquals(HttpStatusCode.OK, call.status)
        assertEquals(getMockUser().toResponse(), response)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostApiAuthUserExists() = testApplication {
        application {
            main()
        }
        whenever(mockAuthUseCase.checkIfMailExists(anyString())).thenReturn(true)

        val call = client.post("/api/auth/userExists") {
            contentType(ContentType.Application.Json)
            body = existsQuery().toString()
        }
        val response = Json.decodeFromString<Map<String, Boolean>>(call.content.awaitContent().toString())
        assertEquals(HttpStatusCode.OK, call.status)
        assertEquals(true, response["userExists"])
    }
}
