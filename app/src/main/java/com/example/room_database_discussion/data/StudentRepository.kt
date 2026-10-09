package com.example.room_database_discussion.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    val students: Flow<List<Student>>
        get() = studentDao.getAllStudents()

    fun searchStudents(search: String): Flow<List<Student>> =
        studentDao.searchStudents(search)

    fun getStudentsByCourse(course: String): Flow<List<Student>> =
        studentDao.getStudentsByCourse(course)

    fun searchStudentsByCourse(search: String, course: String): Flow<List<Student>> =
        studentDao.searchStudentsByCourse(search, course)

    val courses: Flow<List<String>>
        get() = studentDao.getCourses()

    val studentCount: Flow<Int>
        get() = studentDao.getStudentCount()

    suspend fun insertStudents(student: Student){
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student){
        studentDao.deleteStudent(student)
    }

    suspend fun updateStudent(student: Student){
        studentDao.updateStudent(student)
    }
}
