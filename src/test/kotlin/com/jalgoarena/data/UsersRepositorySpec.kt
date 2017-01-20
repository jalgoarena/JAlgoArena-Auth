package com.jalgoarena.data

import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class UsersRepositorySpec {

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
    fun returns_all_available_users() {
        repository.add(user("Mikolaj", "mikolaj@mail.com"))
        repository.add(user("Julia", "julia@mail.com"))

        val users = repository.findAll()
        assertThat(users).size().isGreaterThanOrEqualTo(2)
    }

    @Test
    fun returns_user_for_given_username() {
        repository.add(user("Madzia", "madzia@mail.com"))
        val user = repository.findByUsername("Madzia")
        assertThat(user.email).isEqualTo("madzia@mail.com")
    }

    @Test
    fun sets_user_as_default_role() {
        repository.add(user("Madzia3", "madzia3@mail.com"))
        val user = repository.findByUsername("Madzia3")
        assertThat(user.role).isEqualTo(Role.USER)
    }

    @Test
    fun updates_password_of_the_existing_user() {
        val sam = repository.add(user("Sam", "sam@email.com"))
        val samWithNewPassword = repository.update(sam.copy(password = "new_password"))

        assertThat(sam.password).isNotEqualTo(samWithNewPassword.password)
        assertThat(sam.id).isEqualTo(samWithNewPassword.id)
    }

    @Test
    fun leaves_password_unchanged_when_updating_user_with_empty_password() {
        val dean = repository.add(user("Dean", "dean@email.com"))
        val samWithNewPassword = repository.update(dean.copy(password = "", region = "New_region"))

        assertThat(dean.password).isEqualTo(samWithNewPassword.password)
        assertThat(samWithNewPassword.region).isEqualTo("New_region")
    }

    @Test(expected = UsernameIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_username() {
        repository.add(user("Jacek", "jacek@mail.com"))
        repository.add(user("Jacek", "jacek2@mail.com"))
    }

    @Test(expected = EmailIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_email() {
        repository.add(user("Jacek", "jacek@mail.com"))
        repository.add(user("Jacek2", "jacek@mail.com"))
    }

    private fun user(username: String, email: String)  =
            User(username, "blabla", email, "PL", "Team A")
}
