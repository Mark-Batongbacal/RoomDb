package com.example.room_database_discussion

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.data.StudentDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentCountInstrumentedTest {
    @Test
    fun countTracksInsertDeleteAndUpdate() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, StudentDatabase::class.java).build()

        try {
            val dao = database.studentDao()
            assertEquals(0, dao.getStudentCount().first())

            dao.insertStudent(Student(name = "Juan Dela Cruz", email = "juan@email.com", course = "BSCpE"))
            assertEquals(1, dao.getStudentCount().first())

            dao.insertStudent(Student(name = "Maria Santos", email = "maria@email.com", course = "BSCS"))
            assertEquals(2, dao.getStudentCount().first())

            val juan = dao.getAllStudents().first().first { it.name == "Juan Dela Cruz" }
            dao.updateStudent(juan.copy(name = "Juan Miguel Reyes"))
            assertEquals(2, dao.getStudentCount().first())

            dao.deleteStudent(juan)
            assertEquals(1, dao.getStudentCount().first())
        } finally {
            database.close()
        }
    }
}
