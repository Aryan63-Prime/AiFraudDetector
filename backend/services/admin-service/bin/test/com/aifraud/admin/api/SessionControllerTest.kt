package com.aifraud.admin.api

import com.aifraud.admin.config.AdminAuthProperties
import com.aifraud.admin.model.StartSessionRequest
import com.aifraud.admin.service.SessionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(
    properties = [
        "admin.auth.users[0].username=test",
        "admin.auth.users[0].password=secret",
        "admin.auth.users[0].roles[0]=ANALYST"
    ]
)
@AutoConfigureMockMvc
class SessionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var authProperties: AdminAuthProperties

    @Autowired
    private lateinit var sessionService: SessionService

    @Test
    fun `should create session for valid credentials`() {
        val request = StartSessionRequest(username = "test", password = "secret")

        val result = mockMvc.post("/api/v1/admin/session") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "username": "${request.username}",
                    "password": "${request.password}"
                }
            """.trimIndent()
        }.andReturn()

        assertThat(result.response.status).isEqualTo(201)
        assertThat(result.response.contentAsString).contains("token")

        val session = sessionService.resolve("invalid-token")
        assertThat(session).isNull()
    }

    @Test
    fun `should reject invalid credentials`() {
        val result = mockMvc.post("/api/v1/admin/session") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "username": "wrong",
                    "password": "credentials"
                }
            """.trimIndent()
        }.andReturn()

        assertThat(result.response.status).isEqualTo(401)
    }
}
