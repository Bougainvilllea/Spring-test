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
        wipeOutData()
    }

    private fun wipeOutData() {
        try {
            jdbcTemplate.execute("DELETE FROM dishes")
        } catch (e: Exception) {
        }
        try {
            jdbcTemplate.execute("DELETE FROM restaurants")
        } catch (e: Exception) {
        }
        try {
            jdbcTemplate.execute("ALTER SEQUENCE restaurants_id_seq RESTART WITH 1")
        } catch (e: Exception) {
        }
    }

    @Test
    @DisplayName("POST: Positive - Creates restaurant successfully with status 201")
    fun addNewRestaurant_ValidData_ReturnsCreated() {
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
    fun addNewRestaurant_BlankName_ReturnsBadRequest() {
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
    fun addNewRestaurant_NoNameField_ReturnsBadRequest() {
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
    fun addNewRestaurant_NoAddressField_ReturnsBadRequest() {
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
    fun addNewRestaurant_ExcessiveNameLength_ReturnsBadRequest() {
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

    @Test
    @DisplayName("GET: Positive - Returns list of all restaurants with status 200")
    fun fetchAllRestaurants_ReturnsRestaurantsList() {
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
    fun fetchAllRestaurants_EmptyDatabase_ReturnsEmptyArray() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants")
        } Then {
            statusCode(200)
            body("size()", equalTo(0))
        }
    }

    @Test
    @DisplayName("GET {id}: Positive - Returns restaurant by id with status 200")
    fun fetchRestaurantById_ValidId_ReturnsRestaurant() {
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
    fun fetchRestaurantById_InvalidId_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/999999")
        } Then {
            statusCode(404)
            body("status", equalTo(404))
        }
    }

    @Test
    @DisplayName("PUT: Positive - Updates restaurant successfully with status 200")
    fun modifyRestaurant_ValidData_ReturnsUpdatedEntity() {
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
    fun modifyRestaurant_ChangeOnlyAddress_ReturnsUpdatedEntity() {
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
    fun modifyRestaurant_ChangeOnlyName_ReturnsUpdatedEntity() {
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
    fun modifyRestaurant_NonExistentId_ReturnsNotFound() {
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
    fun modifyRestaurant_BlankName_ReturnsBadRequest() {
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
    fun modifyRestaurant_BlankAddress_ReturnsBadRequest() {
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
    fun modifyRestaurant_DuplicateName_ReturnsConflict() {
        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "First Restaurant", "address": "First Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)

        val secondId = RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Second Restaurant", "address": "Second Address"}""")
            .post("/api/v1/restaurants")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")

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

    @Test
    @DisplayName("DELETE: Negative - Returns 404 when deleting non-existent restaurant")
    fun removeRestaurant_NonExistentId_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            delete("/api/v1/restaurants/999999")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @DisplayName("GET {id}/dishes: Positive - Returns empty list when restaurant has no dishes")
    fun fetchRestaurantDishes_NoDishes_ReturnsEmptyList() {
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
    fun fetchRestaurantDishes_InvalidRestaurantId_ReturnsNotFound() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/api/v1/restaurants/999999/dishes")
        } Then {
            statusCode(404)
        }
    }
}