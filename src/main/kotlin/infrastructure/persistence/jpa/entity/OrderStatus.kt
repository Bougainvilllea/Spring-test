package infrastructure.persistence.jpa.entity

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    DELIVERED,
    CANCELLED
}