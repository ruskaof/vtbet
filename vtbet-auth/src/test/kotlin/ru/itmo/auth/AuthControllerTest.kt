//package ru.itmo.auth
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.test.context.junit.jupiter.SpringExtension
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.post
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
//import ru.itmo.common.request.UserPasswordRequestDto
//
//@ExtendWith(SpringExtension::class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class AuthControllerIntegrationTest {
//
//    @Autowired
//    lateinit var mockMvc: MockMvc
//
//    @Autowired
//    lateinit var objectMapper: ObjectMapper
//
//    @Test
//    fun `should register a new user and return JwtResponseDto`() {
//        // Arrange
//        val request = UserPasswordRequestDto(username = "newuser", password = "password123")
//
//        // Act & Assert
//        mockMvc.post("/auth/register") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }
//            .andExpect {
//                status { isOk() }
//                jsonPath("$.userId").exists()
//                jsonPath("$.token").exists()
//            }
//    }
//
//    @Test
//    fun `should fail to register if username is already taken`() {
//        // Arrange
//        val request = UserPasswordRequestDto(username = "existingUser", password = "password123")
//
//        // Prepopulate the database with a user to simulate an existing username
//        mockMvc.post("/auth/register") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }.andExpect {
//            status { isOk() }
//        }
//
//        // Act & Assert (Attempt to register with the same username again)
//        mockMvc.post("/auth/register") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }
//            .andExpect {
//                status { isConflict() } // Assuming the service throws a 409 Conflict on duplicate username
//                jsonPath("$.message").value("Username already exists") // Example error message
//            }
//    }
//
//    @Test
//    fun `should log in successfully with valid credentials`() {
//        // Arrange
//        val request = UserPasswordRequestDto(username = "testuser", password = "password123")
//
//        // Prepopulate the database by registering the user
//        mockMvc.post("/auth/register") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }.andExpect {
//            status { isOk() }
//        }
//
//        // Act & Assert
//        mockMvc.post("/auth/login") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }
//            .andExpect {
//                status { isOk() }
//                jsonPath("$.userId").exists()
//                jsonPath("$.token").exists()
//            }
//    }
//
//    @Test
//    fun `should fail to log in with invalid credentials`() {
//        // Arrange
//        val request = UserPasswordRequestDto(username = "testuser", password = "wrongpassword")
//
//        // Act & Assert
//        mockMvc.post("/auth/login") {
//            contentType = MediaType.APPLICATION_JSON
//            content = objectMapper.writeValueAsString(request)
//        }
//            .andExpect {
//                status { isUnauthorized() } // Assuming 401 Unauthorized for invalid credentials
//                jsonPath("$.message").value("Invalid username or password") // Example error message
//            }
//    }
//}
