package com.example.infrastructure.adapter.persistence.mock
import com.example.domain.model.common.IHaveID
import com.example.domain.port.BaseRepositoryPort
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/** Адаптеры - эти типо DAO, у меня ушло полчаса чтобы до этого допереть*/


abstract class BaseMockRepositoryAdapter<T: IHaveID<T>> : BaseRepositoryPort<T> {
    protected val storage = ConcurrentHashMap<Long, T>()
    private val idGenerator = AtomicLong(0)

    override fun findAll(): List<T> {
        return storage.values.toList()
    }

    override fun findById(id: Long): T? {
        return storage[id]
    }

    override fun create(entity: T): T{
        val id = entity.id ?: idGenerator.getAndIncrement()
        val newEntity = entity.withId(id)
        storage[id] = newEntity
        return newEntity
    }

    override fun update(entity: T): T {
        val id = entity.id ?: throw IllegalArgumentException("Cannot update entity without ID")

        storage[id] = entity
        return entity
    }

    override fun deleteById(id: Long): Boolean{
        val result = storage.remove(id) != null
        return result
    }
}