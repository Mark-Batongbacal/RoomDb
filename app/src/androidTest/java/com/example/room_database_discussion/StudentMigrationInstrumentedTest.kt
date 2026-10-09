package com.example.room_database_discussion

import android.content.Context
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
class StudentMigrationInstrumentedTest {
    @Test
    fun migrationPreservesStudentsAndAddsAge() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseName = "student_migration_test.db"
        context.deleteDatabase(databaseName)

        try {
            context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null).use { legacy ->
                legacy.execSQL("""
                    CREATE TABLE students (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        course TEXT NOT NULL
                    )
                """.trimIndent())
                legacy.execSQL(
                    "INSERT INTO students (name, email, course) VALUES (?, ?, ?)",
                    arrayOf("Juan Dela Cruz", "juan@email.com", "BSCpE")
                )
                legacy.version = 1
            }

            val database = Room.databaseBuilder(context, StudentDatabase::class.java, databaseName)
                .addMigrations(StudentDatabase.MIGRATION_1_2)
                .build()
            try {
                val dao = database.studentDao()
                val existing = dao.getAllStudents().first().single()
                assertEquals("Juan Dela Cruz", existing.name)
                assertEquals("juan@email.com", existing.email)
                assertEquals("BSCpE", existing.course)
                assertEquals(0, existing.age)

                dao.updateStudent(existing.copy(age = 21))
                assertEquals(21, dao.getAllStudents().first().single().age)

                dao.insertStudent(Student(name = "Maria Santos", email = "maria@email.com", course = "BSCS", age = 20))
                val students = dao.getAllStudents().first()
                assertEquals(2, students.size)
                assertEquals(20, students.single { it.name == "Maria Santos" }.age)
            } finally {
                database.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }
}
