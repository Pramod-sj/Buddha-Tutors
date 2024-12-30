package com.buddhatutors.common.data.data.model

import com.buddhatutors.common.data.EntityMapper
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import javax.inject.Inject

data class UserEntity(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val userType: Int = -1
)

class UserEMapper @Inject constructor() : EntityMapper<UserEntity, User> {

    override fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            userType = UserType.entries.find { it.id == entity.userType } ?: UserType.STUDENT
        )
    }

    override fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            userType = domain.userType.id
        )
    }

}