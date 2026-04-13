package com.example.infrastructure.adapter.controller

import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class RestaurantControllerIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        @Suppress("unused")
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("restaurant-test-db")
            withUsername("test")
            withPassword("test")
        }
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var dataSource: DataSource

    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc)
        jdbcTemplate = JdbcTemplate(dataSource)
        cleanDatabase()
    }

    private fun cleanDatabase() {
        // Очищаем таблицы в правильном порядке (сначала дочерние, потом родительские)
        try {
            jdbcTemplate.execute("DELETE FROM dishes")
        } catch (e: Exception) {
            // Таблица dishes может не существовать, игнорируем
        }
        try {
            jdbcTemplate.execute("DELETE FROM restaurants")
        } catch (e: Exception) {
            // Таблица restaurants может не существовать, игнорируем
        }
        // Сбрасываем последовательности (опционально)
        try {
            jdbcTemplate.execute("ALTER SEQUENCE restaurants_id_seq RESTART WITH 1")
        } catch (e: Exception) {
            // Игнорируем, если последовательность не существует
        }
    }

    // POST /api/v1/restaurants
    @Test
    @DisplayName("POST: Positive - Creates restaurant successfully with status 201")
    fun createRestaurant_ValidRequest_ReturnsCreated() {
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "Italian Bistro", "address": "123 Main Street"}""")
        } When {
            post("/api/v1/restaurants")
        } Then {
            statusCode(201)
            body("id", notNullValue())
            body("name", equalTo("Italian Bistro"))
            body("address", equalTo("123 Main Street"))
        }
    }

    @Test
    @DisplayName("POST: Negative - Returns 400 when restaurant name is empty")
    fun createRestaurant_EmptyName_ReturnsBadRequest() {
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "", "address": "123 Main Street"}""")
        } When {
            post("/api/v1/restaurants")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }

    @Test
    @DisplayName("POST: Negative - Returns 400 when name is missing")
    fun createRestaurant_MissingName_ReturnsBadRequest() {
        Given {
            contentType(ContentType.JSON)
            body("""{"address": "123 Main Street"}""")
        } When {
            post("/api/v1/restaurants")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }

    @Test
    @DisplayName("POST: Negative - Returns 400 when address is missing")
    fun createRestaurant_MissingAddress_ReturnsBadRequest() {
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "Italian Bistro"}""")
        } When {
            post("/api/v1/restaurants")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }

    @Test
    @DisplayName("POST: Negative - Returns 400 when name is too long")
    fun createRestaurant_NameTooLong_ReturnsBadRequest() {
        val longName = "A".repeat(256)
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "$longName", "address": "123 Main Street"}""")
        } When {
            post("/api/v1/restaurants")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }


    // GET /api/v1/restaurants
    @Test
    @DisplayName("GET: Positive - Returns list of all restaurants with status 200")
    fun getAllRestaurants_ReturnsListOfRestaurants() {
        // Create two restaurants first
        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Sushi House", "address": "5 Ocean Drive"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)

        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Pizza Palace", "address": "10 Pizza Lane"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)

        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants")
        } Then {
            statusCode(200)
            body("size()", equalTo(2))
            body("[0].name", anyOf(equalTo("Sushi House"), equalTo("Pizza Palace")))
            body("[1].name", anyOf(equalTo("Sushi House"), equalTo("Pizza Palace")))
        }
    }

    @Test
    @DisplayName("GET: Positive - Returns empty list when no restaurants exist")
    fun getAllRestaurants_NoRestaurants_ReturnsEmptyList() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants")
        } Then {
            statusCode(200)
            body("size()", equalTo(0))
        }
    }

    // GET /api/v1/restaurants/{id}
    @Test
    @DisplayName("GET {id}: Positive - Returns restaurant by id with status 200")
    fun getRestaurantById_ExistingId_ReturnsRestaurant() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Burger King", "address": "77 Fast Food Ave"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/$id")
        } Then {
            statusCode(200)
            body("id", equalTo(id))
            body("name", equalTo("Burger King"))
            body("address", equalTo("77 Fast Food Ave"))
        }
    }

    @Test
    @DisplayName("GET {id}: Negative - Returns 404 when restaurant not found")
    fun getRestaurantById_NonExistentId_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/999999")
        } Then {
            statusCode(404)
            body("status", equalTo(404))
        }
    }

    // PUT /api/v1/restaurants/{id}
    @Test
    @DisplayName("PUT: Positive - Updates restaurant successfully with status 200")
    fun updateRestaurant_ValidRequest_ReturnsUpdatedRestaurant() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Old Restaurant", "address": "Old Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            contentType(ContentType.JSON)
            body("""{"name": "New Restaurant Name", "address": "New Updated Address"}""")
        } When {
            put("/api/v1/restaurants/$id")
        } Then {
            statusCode(200)
            body("id", equalTo(id))
            body("name", equalTo("New Restaurant Name"))
            body("address", equalTo("New Updated Address"))
        }
    }

    @Test
    @DisplayName("PUT: Positive - Updates restaurant with same name but new address")
    fun updateRestaurant_UpdateAddressOnly_ReturnsUpdatedRestaurant() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Original Name", "address": "Original Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            contentType(ContentType.JSON)
            body("""{"name": "Original Name", "address": "Updated Address Only"}""")
        } When {
            put("/api/v1/restaurants/$id")
        } Then {
            statusCode(200)
            body("id", equalTo(id))
            body("name", equalTo("Original Name"))
            body("address", equalTo("Updated Address Only"))
        }
    }

    @Test
    @DisplayName("PUT: Positive - Updates restaurant with new name but same address")
    fun updateRestaurant_UpdateNameOnly_ReturnsUpdatedRestaurant() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Original Name", "address": "Original Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            contentType(ContentType.JSON)
            body("""{"name": "Updated Name Only", "address": "Original Address"}""")
        } When {
            put("/api/v1/restaurants/$id")
        } Then {
            statusCode(200)
            body("id", equalTo(id))
            body("name", equalTo("Updated Name Only"))
            body("address", equalTo("Original Address"))
        }
    }

    @Test
    @DisplayName("PUT: Negative - Returns 404 when updating non-existent restaurant")
    fun updateRestaurant_NonExistentId_ReturnsNotFound() {
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "Any Name", "address": "Any Address"}""")
        } When {
            put("/api/v1/restaurants/999999")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @DisplayName("PUT: Negative - Returns 400 when updating with empty name")
    fun updateRestaurant_EmptyName_ReturnsBadRequest() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Valid Name", "address": "Valid Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            contentType(ContentType.JSON)
            body("""{"name": "", "address": "New Address"}""")
        } When {
            put("/api/v1/restaurants/$id")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }

    @Test
    @DisplayName("PUT: Negative - Returns 400 when updating with empty address")
    fun updateRestaurant_EmptyAddress_ReturnsBadRequest() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Valid Name", "address": "Valid Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            contentType(ContentType.JSON)
            body("""{"name": "New Name", "address": ""}""")
        } When {
            put("/api/v1/restaurants/$id")
        } Then {
            statusCode(400)
            body("message", notNullValue())
        }
    }

    @Test
    @DisplayName("PUT: Negative - Returns 409 when updating to an existing restaurant name")
    fun updateRestaurant_DuplicateName_ReturnsConflict() {
        // Create first restaurant
        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "First Restaurant", "address": "First Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)

        // Create second restaurant
        val secondId = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Second Restaurant", "address": "Second Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        // Try to update second restaurant with first restaurant's name
        Given {
            contentType(ContentType.JSON)
            body("""{"name": "First Restaurant", "address": "Updated Address"}""")
        } When {
            put("/api/v1/restaurants/$secondId")
        } Then {
            statusCode(409)
            body("message", containsString("already exists"))
        }
    }

    // DELETE /api/v1/restaurants/{id}
    @Test
    @DisplayName("DELETE: Positive - Deletes restaurant successfully with status 204 and verifies it's gone")
    fun deleteRestaurant_ExistingId_ReturnsNoContent() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Restaurant To Delete", "address": "Delete Me Street"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            accept(ContentType.JSON)
        } When {
            delete("/api/v1/restaurants/$id")
        } Then {
            statusCode(204)
        }

        // Verify restaurant no longer exists
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/$id")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @DisplayName("DELETE: Negative - Returns 404 when deleting non-existent restaurant")
    fun deleteRestaurant_NonExistentId_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            delete("/api/v1/restaurants/999999")
        } Then {
            statusCode(404)
        }
    }

    // GET /api/v1/restaurants/{id}/dishes
    @Test
    @DisplayName("GET {id}/dishes: Positive - Returns empty list when restaurant has no dishes")
    fun getRestaurantDishes_NoDishes_ReturnsEmptyList() {
        val id = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Empty Dishes Restaurant", "address": "No Food Street"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/$id/dishes")
        } Then {
            statusCode(200)
            body("size()", equalTo(0))
        }
    }

    @Test
    @DisplayName("GET {id}/dishes: Negative - Returns 404 when restaurant not found")
    fun getRestaurantDishes_NonExistentRestaurant_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/999999/dishes")
        } Then {
            statusCode(404)
        }
    }
}