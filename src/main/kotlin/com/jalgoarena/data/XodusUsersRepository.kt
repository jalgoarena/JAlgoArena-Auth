package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.PersistentStoreTransaction
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class XodusUsersRepository(dbName: String) : UsersRepository {

    constructor() : this(Constants.storePath)

    private val LOG = LoggerFactory.getLogger(this.javaClass)
    private val ADMIN_USERNAME = "admin"

    private val store = PersistentEntityStores.newInstance(dbName)

    init {
        try {
            findByUsername(ADMIN_USERNAME)
        } catch (ex: UsernameNotFoundException) {
            LOG.info("Admin does not exist.")
            createAdminUser()
        }
    }

    override fun findAll(): List<User> {
        return readonly {
            it.getAll(Constants.entityType).map { User.from(it) }
        }
    }

    override fun findByUsername(username: String): User {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.username,
                    username
            ).map { User.from(it) }.firstOrNull()
        } ?: throw UsernameNotFoundException("User not found: $username")
    }

    override fun destroy() {
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
        return store.computeInTransaction { call(it as PersistentStoreTransaction) }
    }

    private fun <T> readonly(call: (PersistentStoreTransaction) -> T): T {
        return store.computeInReadonlyTransaction { call(it as PersistentStoreTransaction) }
    }

    override fun add(user: User): User {
        return transactional {
            checkIfUsernameOrEmailIsAlreadyUsed(it, user)

            val entity = it.newEntity(Constants.entityType)
            updateEntity(entity, user)
        }
    }

    override fun update(user: User): User {
        return transactional {
            val entityId = it.toEntityId(user.id!!)
            val entity = it.getEntity(entityId)
            updateEntity(entity, user)
        }
    }

    private fun updateEntity(entity: Entity, user: User): User {
        entity.apply {
            setProperty(Constants.username, user.username)
            if (!user.password.isNullOrBlank()) setProperty(
                    Constants.password,
                    BCryptPasswordEncoder().encode(user.password)
            )
            setProperty(Constants.email, user.email)
            setProperty(Constants.region, user.region)
            setProperty(Constants.team, user.team)
            setProperty(Constants.role, user.role.name)
        }

        return User.from(entity)
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

    private fun createAdminUser() {
        val randomPassword = UUID.randomUUID().toString()

        LOG.info("""

    Admin user created, password: $randomPassword. Please change after first time log in

""")

        add(User(
                username = ADMIN_USERNAME,
                password = randomPassword,
                email = "admin@mail.com",
                region = "Kraków",
                team = "Admin",
                role = Role.ADMIN
        ))
    }
}
