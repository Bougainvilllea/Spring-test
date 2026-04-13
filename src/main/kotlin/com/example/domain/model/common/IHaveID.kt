package com.example.domain.model.common

interface IHaveID<T: IHaveID<T>> {
    val id: Long?

    fun withId(id: Long): T
}