package com.example.room_database_discussion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.data.StudentDatabase
import com.example.room_database_discussion.data.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StudentViewModel(application: Application): AndroidViewModel(application) {
    private val database = StudentDatabase.getData(application)
    private val repository = StudentRepository(database.studentDao())
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCourse = MutableStateFlow<String?>(null)
    private val _courses = MutableStateFlow<List<String>>(emptyList())
    private val _studentCount = MutableStateFlow(0)

    val students: StateFlow<List<Student>> = _students.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val selectedCourse: StateFlow<String?> = _selectedCourse.asStateFlow()
    val courses: StateFlow<List<String>> = _courses.asStateFlow()
    val studentCount: StateFlow<Int> = _studentCount.asStateFlow()

    init{
        viewModelScope.launch{
            combine(_searchQuery, _selectedCourse) { search, course -> search to course }
                .collectLatest { (search, course) ->
                    val results = when {
                        course == null && search.isBlank() -> repository.students
                        course == null -> repository.searchStudents(search)
                        search.isBlank() -> repository.getStudentsByCourse(course)
                        else -> repository.searchStudentsByCourse(search, course)
                    }
                    results.collect { studentList ->
                        _students.value = studentList
                    }
                }
        }
        viewModelScope.launch {
            repository.courses.collect { _courses.value = it }
        }
        viewModelScope.launch {
            repository.studentCount.collect { _studentCount.value = it }
        }
    }

    fun searchStudents(search: String) {
        _searchQuery.value = search
    }

    fun filterByCourse(course: String?) {
        _selectedCourse.value = course
    }

    fun addStudent(name: String, email: String, course: String, age: Int){
        viewModelScope.launch {
            val student = Student(
                name = name,
                email = email,
                course = course,
                age = age
            )

            repository.insertStudents(student)
        }
    }

    fun deleteStudent(student: Student){
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }
}
