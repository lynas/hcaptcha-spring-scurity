package com.lynas.hcaptchaspringmvc

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "app_user")
class AppUser {
    @Id
    lateinit var id: String
    @Column(nullable = false, unique = true)
    lateinit var username: String
    @Column(nullable = false)
    lateinit var password: String
}

data class AppUserDto(
    val username: String,
    val password: String
)


interface UserRepository : JpaRepository<AppUser, String> {
    fun findByUsername(username: String): AppUser?
}