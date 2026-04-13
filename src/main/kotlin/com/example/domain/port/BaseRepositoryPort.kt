package com.example.domain.port
import com.example.domain.model.common.IHaveID

interface BaseRepositoryPort<T: IHaveID<T>> {
    fun findAll(): List<T>
    fun findById(id: Long): T?
    fun update(entity: T): T
    fun deleteById(id: Long): Boolean
    fun create(entity: T): T
}
