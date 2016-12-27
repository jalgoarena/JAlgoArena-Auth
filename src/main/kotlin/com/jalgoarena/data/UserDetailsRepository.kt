package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.User
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.PersistentStoreTransaction
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Repository
import javax.annotation.PreDestroy

@Repository
class UserDetailsRepository(dbName: String) {

    constructor() : this(Constants.storePath)

    private val LOG = LoggerFactory.getLogger(this.javaClass)
    private val store: PersistentEntityStore = PersistentEntityStores.newInstance(dbName)

    fun findAll(): List<User> {
        return readonly {
            it.getAll(Constants.entityType).map { User.from(it) }
        }
    }

    fun findByUsername(username: String): User? {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.username,
                    username
            ).map { User.from(it) }.firstOrNull()
        }
    }

    @PreDestroy
    fun destroy() {
        var proceed = true
        var count = 1
        while (proceed && count <= 10) {
            try {
                LOG.info("trying to close persistent store. attempt {}", count)
                store.close()
                proceed = false
                LOG.info("persistent store closed")
            } catch (e: RuntimeException) {
                LOG.error("error closing persistent store", e)
                count++
            }
        }
    }

    private fun <T> transactional(call: (PersistentStoreTransaction) -> T): T {
        return transactional(store, call)
    }

    private fun <T> readonly(call: (PersistentStoreTransaction) -> T): T {
        return readonly(store, call)
    }

    fun addUser(user: User) {
        transactional {
            checkIfUsernameOrEmailIsAlreadyUsed(it, user)

            it.newEntity(Constants.entityType).apply {
                setProperty(Constants.username, user.username)
                setProperty(
                        Constants.password,
                        BCryptPasswordEncoder().encode(user.password)
                )
                setProperty(Constants.email, user.email)
                setProperty(Constants.region, user.region)
                setProperty(Constants.team, user.team)
                setProperty(Constants.role, user.role)
            }
        }
    }

    private fun checkIfUsernameOrEmailIsAlreadyUsed(it: PersistentStoreTransaction, user: User) {
        val usernameAlreadyUsed =
                it.find(Constants.entityType, Constants.username, user.username).firstOrNull()
        if (usernameAlreadyUsed != null)
            throw UsernameIsAlreadyUsedException()

        val emailAlreadyUsed =
                it.find(Constants.entityType, Constants.email, user.email).firstOrNull()
        if (emailAlreadyUsed != null)
            throw EmailIsAlreadyUsedException()
    }
}

fun <T> transactional(store: PersistentEntityStore, call: (PersistentStoreTransaction) -> T): T {
    return store.computeInTransaction { call(it as PersistentStoreTransaction) }
}

fun <T> readonly(store: PersistentEntityStore, call: (PersistentStoreTransaction) -> T): T {
    return store.computeInReadonlyTransaction { call(it as PersistentStoreTransaction) }
}
