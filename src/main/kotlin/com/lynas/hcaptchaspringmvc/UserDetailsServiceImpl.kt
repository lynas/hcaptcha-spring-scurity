package com.lynas.hcaptchaspringmvc

//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.stereotype.Service
//
//@Service
//class UserDetailsServiceImpl( private val userRepository: UserRepository) : UserDetailsService {
//    override fun loadUserByUsername(username: String): UserDetails {
//        val user = userRepository.findByUsername(username)
//            ?: throw UsernameNotFoundException("Could not find user")
//
//        return User.withUsername(user.username)
//            .password(user.password)
//            .roles("USER") // You can specify the roles here
//            .build()
//    }
//}
