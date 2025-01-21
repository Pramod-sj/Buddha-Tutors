package com.buddhatutors.core.data

interface EntityMapper<Entity, Domain> {

    fun toEntity(domain: Domain): Entity

    fun toDomain(entity: Entity): Domain

}