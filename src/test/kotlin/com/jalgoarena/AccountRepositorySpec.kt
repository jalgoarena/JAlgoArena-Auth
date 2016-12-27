package com.jalgoarena

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.domain.User
import com.winterbe.expekt.should
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class AccountRepositorySpec {

    companion object {
        val dbName = "./UserDetailsStoreForTests"
        var repository: UserDetailsRepository

        init {
            val store = PersistentEntityStores.newInstance(dbName)
            store.close()
            repository = UserDetailsRepository(dbName)
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            repository.destroy()
            File(dbName).deleteRecursively()
        }
    }

    @Test
    fun should_return_all_available_problems() {
        repository.addUser(sampleUser("Mikolaj", "mikolaj@mail.com"))
        repository.addUser(sampleUser("Julia", "julia@mail.com"))

        val users = repository.findAll()
        users.should.have.size.least(2)
    }

    @Test
    fun should_return_particular_problem() {
        repository.addUser(sampleUser("Madzia", "madzia@mail.com"))
        val user = repository.findByUsername("Madzia")!!
        user.email.should.equal("madzia@mail.com")
    }

    @Test(expected = UsernameIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_username() {
        repository.addUser(sampleUser("Jacek", "jacek@mail.com"))
        repository.addUser(sampleUser("Jacek", "jacek2@mail.com"))
    }

    @Test(expected = EmailIsAlreadyUsedException::class)
    fun throws_exception_if_there_is_user_with_same_email() {
        repository.addUser(sampleUser("Jacek", "jacek@mail.com"))
        repository.addUser(sampleUser("Jacek2", "jacek@mail.com"))
    }

    private val sampleUser =
            { username: String, email: String -> User(username, "blabla", email, "PL", "Team A") }
}
