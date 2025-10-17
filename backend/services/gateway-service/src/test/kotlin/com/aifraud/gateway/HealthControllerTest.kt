package com.aifraud.gateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthController::class)
class HealthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return health status`() {
        val result = mockMvc.get("/health") {
            accept = MediaType.APPLICATION_JSON
        }
            .andReturn()

        assertThat(result.response.status).isEqualTo(200)
        assertThat(result.response.contentAsString).contains("\"status\":\"UP\"")
    }
}
