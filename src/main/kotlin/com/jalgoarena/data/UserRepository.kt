package com.jalgoarena.data

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional(Transactional.TxType.MANDATORY)
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): List<User>
}
