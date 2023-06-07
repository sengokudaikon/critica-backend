package net.critika.ports

import kotlinx.uuid.UUID


interface CrudPort<T, R> {
    suspend fun create(command: T): R
    suspend fun update(command: T): R
    suspend fun delete(id: UUID)
    suspend fun get(id: UUID): R
    suspend fun list(): List<R>
}
