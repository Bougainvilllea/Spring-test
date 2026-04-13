package com.example.application.service

import com.example.domain.model.Dish
import com.example.domain.model.User
import com.example.domain.port.DishRepositoryPort
import com.example.domain.port.UserRepositoryPort
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.port.OrderRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import com.example.shared.exception.BusinessException
import java.math.BigDecimal
import java.time.LocalDateTime
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
@DisplayName("OrderService Unit Tests")
class OrderServiceImplTest {

    @Mock
    private lateinit var orderRepositoryPort: OrderRepositoryPort

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @Mock
    private lateinit var dishRepositoryPort: DishRepositoryPort

    @InjectMocks
    private lateinit var orderService: OrderServiceImpl

    private val sampleUser = User(
        id = 1L,
        email = "test@example.com",
        firstName = "John",
        lastName = "Doe",
        isActive = true
    )

    private val sampleDish1 = Dish(
        id = 1L,
        name = "Pizza",
        description = "Delicious pizza",
        price = BigDecimal.valueOf(15.99),
        isAvailable = true,
        restaurantId = 1L
    )

    private val sampleDish2 = Dish(
        id = 2L,
        name = "Pasta",
        description = "Italian pasta",
        price = BigDecimal.valueOf(12.99),
        isAvailable = true,
        restaurantId = 1L
    )

    private val sampleOrder = Order(
        id = 1L,
        userId = 1L,
        status = OrderStatus.PENDING,
        createdAt = LocalDateTime.now(),
        dishes = listOf(sampleDish1, sampleDish2)
    )

    @Test
    @DisplayName("Should return order by id when exists")
    fun fetchOrderById_ValidId_ReturnsOrder() {
        `when`(orderRepositoryPort.findById(1L)).thenReturn(sampleOrder)

        val outcome = orderService.getOrderById(1L)

        assertEquals(sampleOrder, outcome)
        verify(orderRepositoryPort).findById(1L)
    }

    @Test
    @DisplayName("Should throw OrderNotFound when order does not exist")
    fun fetchOrderById_InvalidId_ThrowsError() {
        `when`(orderRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.OrderNotFound> {
            orderService.getOrderById(999L)
        }

        assertEquals("Order with id 999 not found", exception.message)
        verify(orderRepositoryPort).findById(999L)
    }

    @Test
    @DisplayName("Should return all orders when no filters provided")
    fun fetchAllOrders_NoFilters_ReturnsAll() {
        val orders = listOf(sampleOrder, sampleOrder.copy(id = 2L))
        `when`(orderRepositoryPort.findAll()).thenReturn(orders)

        val outcome = orderService.getAllOrders(null, null)

        assertEquals(2, outcome.size)
        verify(orderRepositoryPort).findAll()
        verify(orderRepositoryPort, never()).findAllByUserId(any())
        verify(orderRepositoryPort, never()).findAllByStatus(any())
        verify(orderRepositoryPort, never()).findAllByUserIdAndStatus(any(), any())
    }

    @Test
    @DisplayName("Should return orders filtered by userId")
    fun fetchAllOrders_WithUserId_ReturnsFiltered() {
        val orders = listOf(sampleOrder)
        `when`(orderRepositoryPort.findAllByUserId(1L)).thenReturn(orders)

        val outcome = orderService.getAllOrders(1L, null)

        assertEquals(1, outcome.size)
        verify(orderRepositoryPort).findAllByUserId(1L)
        verify(orderRepositoryPort, never()).findAll()
        verify(orderRepositoryPort, never()).findAllByStatus(any())
        verify(orderRepositoryPort, never()).findAllByUserIdAndStatus(any(), any())
    }

    @Test
    @DisplayName("Should return orders filtered by status")
    fun fetchAllOrders_WithStatus_ReturnsFiltered() {
        val orders = listOf(sampleOrder)
        `when`(orderRepositoryPort.findAllByStatus(OrderStatus.PENDING)).thenReturn(orders)

        val outcome = orderService.getAllOrders(null, OrderStatus.PENDING)

        assertEquals(1, outcome.size)
        verify(orderRepositoryPort).findAllByStatus(OrderStatus.PENDING)
        verify(orderRepositoryPort, never()).findAll()
        verify(orderRepositoryPort, never()).findAllByUserId(any())
        verify(orderRepositoryPort, never()).findAllByUserIdAndStatus(any(), any())
    }

    @Test
    @DisplayName("Should return orders filtered by userId and status")
    fun fetchAllOrders_WithUserIdAndStatus_ReturnsFiltered() {
        val orders = listOf(sampleOrder)
        `when`(orderRepositoryPort.findAllByUserIdAndStatus(1L, OrderStatus.PENDING)).thenReturn(orders)

        val outcome = orderService.getAllOrders(1L, OrderStatus.PENDING)

        assertEquals(1, outcome.size)
        verify(orderRepositoryPort).findAllByUserIdAndStatus(1L, OrderStatus.PENDING)
        verify(orderRepositoryPort, never()).findAll()
        verify(orderRepositoryPort, never()).findAllByUserId(any())
        verify(orderRepositoryPort, never()).findAllByStatus(any())
    }

    @Test
    @DisplayName("Should create order successfully")
    fun placeNewOrder_ValidData_CreatesOrder() {
        val dishIds = listOf(1L, 2L)

        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)
        `when`(dishRepositoryPort.findById(1L)).thenReturn(sampleDish1)
        `when`(dishRepositoryPort.findById(2L)).thenReturn(sampleDish2)
        `when`(orderRepositoryPort.create(any())).thenReturn(sampleOrder)

        val outcome = orderService.createOrder(1L, dishIds)

        assertEquals(sampleOrder, outcome)
        verify(userRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findById(2L)
        verify(orderRepositoryPort).create(any())
    }

    @Test
    @DisplayName("Should throw OrderValidationError when user not found")
    fun placeNewOrder_MissingUser_ThrowsError() {
        val dishIds = listOf(1L)

        `when`(userRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.createOrder(999L, dishIds)
        }

        assertEquals("User with id 999 not found", exception.message)
        verify(userRepositoryPort).findById(999L)
        verify(dishRepositoryPort, never()).findById(any())
        verify(orderRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should throw OrderValidationError when dish list is empty")
    fun placeNewOrder_EmptyDishList_ThrowsError() {
        val dishIds = emptyList<Long>()

        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.createOrder(1L, dishIds)
        }

        assertEquals("Order must contain at least one dish", exception.message)
        verify(userRepositoryPort).findById(1L)
        verify(dishRepositoryPort, never()).findById(any())
        verify(orderRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should throw OrderValidationError when dish not found")
    fun placeNewOrder_MissingDish_ThrowsError() {
        val dishIds = listOf(1L, 999L)

        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)
        `when`(dishRepositoryPort.findById(1L)).thenReturn(sampleDish1)
        `when`(dishRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.createOrder(1L, dishIds)
        }

        assertEquals("Dish with id 999 not found", exception.message)
        verify(userRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findById(999L)
        verify(orderRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should update order status from PENDING to CONFIRMED")
    fun changeOrderStatus_PendingToConfirmed_UpdatesSuccessfully() {
        val pendingOrder = sampleOrder.copy(status = OrderStatus.PENDING)
        val confirmedOrder = sampleOrder.copy(status = OrderStatus.CONFIRMED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(pendingOrder)
        `when`(orderRepositoryPort.update(any())).thenReturn(confirmedOrder)

        val outcome = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)

        assertEquals(OrderStatus.CONFIRMED, outcome.status)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort).update(any())
    }

    @Test
    @DisplayName("Should update order status from PENDING to CANCELLED")
    fun changeOrderStatus_PendingToCancelled_UpdatesSuccessfully() {
        val pendingOrder = sampleOrder.copy(status = OrderStatus.PENDING)
        val cancelledOrder = sampleOrder.copy(status = OrderStatus.CANCELLED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(pendingOrder)
        `when`(orderRepositoryPort.update(any())).thenReturn(cancelledOrder)

        val outcome = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED)

        assertEquals(OrderStatus.CANCELLED, outcome.status)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort).update(any())
    }

    @Test
    @DisplayName("Should update order status from CONFIRMED to DELIVERED")
    fun changeOrderStatus_ConfirmedToDelivered_UpdatesSuccessfully() {
        val confirmedOrder = sampleOrder.copy(status = OrderStatus.CONFIRMED)
        val deliveredOrder = sampleOrder.copy(status = OrderStatus.DELIVERED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(confirmedOrder)
        `when`(orderRepositoryPort.update(any())).thenReturn(deliveredOrder)

        val outcome = orderService.updateOrderStatus(1L, OrderStatus.DELIVERED)

        assertEquals(OrderStatus.DELIVERED, outcome.status)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort).update(any())
    }

    @Test
    @DisplayName("Should update order status from CONFIRMED to CANCELLED")
    fun changeOrderStatus_ConfirmedToCancelled_UpdatesSuccessfully() {
        val confirmedOrder = sampleOrder.copy(status = OrderStatus.CONFIRMED)
        val cancelledOrder = sampleOrder.copy(status = OrderStatus.CANCELLED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(confirmedOrder)
        `when`(orderRepositoryPort.update(any())).thenReturn(cancelledOrder)

        val outcome = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED)

        assertEquals(OrderStatus.CANCELLED, outcome.status)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort).update(any())
    }

    @Test
    @DisplayName("Should throw InvalidOrderStatusTransition when changing from PENDING to DELIVERED")
    fun changeOrderStatus_PendingToDelivered_ThrowsError() {
        val pendingOrder = sampleOrder.copy(status = OrderStatus.PENDING)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(pendingOrder)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.updateOrderStatus(1L, OrderStatus.DELIVERED)
        }

        assertTrue(exception.message?.contains("PENDING") == true)
        assertTrue(exception.message?.contains("DELIVERED") == true)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw OrderValidationError when changing status of DELIVERED order")
    fun changeOrderStatus_DeliveredToAnything_ThrowsError() {
        val deliveredOrder = sampleOrder.copy(status = OrderStatus.DELIVERED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(deliveredOrder)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.updateOrderStatus(1L, OrderStatus.CANCELLED)
        }

        assertTrue(exception.message?.contains("Cannot change status") == true)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw OrderValidationError when changing status of CANCELLED order")
    fun changeOrderStatus_CancelledToAnything_ThrowsError() {
        val cancelledOrder = sampleOrder.copy(status = OrderStatus.CANCELLED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(cancelledOrder)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)
        }

        assertTrue(exception.message?.contains("Cannot change status") == true)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw OrderNotFound when updating status of non-existent order")
    fun changeOrderStatus_MissingOrder_ThrowsError() {
        `when`(orderRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.OrderNotFound> {
            orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED)
        }

        assertEquals("Order with id 999 not found", exception.message)
        verify(orderRepositoryPort).findById(999L)
        verify(orderRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw InvalidOrderStatusTransition when changing from CONFIRMED to PENDING")
    fun changeOrderStatus_ConfirmedToPending_ThrowsError() {
        val confirmedOrder = sampleOrder.copy(status = OrderStatus.CONFIRMED)

        `when`(orderRepositoryPort.findById(1L)).thenReturn(confirmedOrder)

        val exception = assertThrows<BusinessException.OrderValidationError> {
            orderService.updateOrderStatus(1L, OrderStatus.PENDING)
        }

        assertTrue(exception.message?.contains("CONFIRMED") == true)
        assertTrue(exception.message?.contains("PENDING") == true)
        verify(orderRepositoryPort).findById(1L)
        verify(orderRepositoryPort, never()).update(any())
    }
}