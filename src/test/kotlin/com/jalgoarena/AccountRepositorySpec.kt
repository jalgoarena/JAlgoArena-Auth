package com.jalgoarena

import com.jalgoarena.data.*
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class AccountRepositorySpec {

    companion object {
        val dbName = "./UserDetailsStoreForTests"
        var repository: UsersRepository

        init {
            val store = PersistentEntityStores.newInstance(dbName)
            store.close()
            repository = XodusUsersRepository(dbName)
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            repository.destroy()
            File(dbName).deleteRecursively()
        }
    }

    @Test
    fun should_return_all_available_users() {
        repository.addUser(user("Mikolaj", "mikolaj@mail.com"))
        repository.addUser(user("Julia", "julia@mail.com"))

        val users = repository.findAll()
        assertThat(users).size().isGreaterThanOrEqualTo(2)
    }

    @Test
    fun should_return_user_for_given_username() {
        repository.addUser(user("Madzia", "madzia@mail.com"))
        val user = repository.findByUsername("Madzia")
        assertThat(user.email).isEqualTo("madzia@mail.com")
    }

    @Test
    fun should_set_user_as_default_role() {
        repository.addUser(user("Madzia3", "madzia3@mail.com"))
        val user = repository.findByUsername("Madzia3")
        assertThat(user.role).isEqualTo(Role.USER)
    }

    @Test(expected = UsernameIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_username() {
        repository.addUser(user("Jacek", "jacek@mail.com"))
        repository.addUser(user("Jacek", "jacek2@mail.com"))
    }

    @Test(expected = EmailIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_email() {
        repository.addUser(user("Jacek", "jacek@mail.com"))
        repository.addUser(user("Jacek2", "jacek@mail.com"))
    }

    private fun user(username: String, email: String)  =
            User(username, "blabla", email, "PL", "Team A")
}
