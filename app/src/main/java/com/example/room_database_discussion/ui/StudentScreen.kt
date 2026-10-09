package com.example.room_database_discussion.ui

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.ui.theme.Charcoal
import com.example.room_database_discussion.ui.theme.MutedInk
import com.example.room_database_discussion.ui.theme.PaleSage
import com.example.room_database_discussion.ui.theme.SoftOutline
import com.example.room_database_discussion.ui.theme.SurfaceWhite
import com.example.room_database_discussion.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val studentCount by viewModel.studentCount.collectAsStateWithLifecycle()

    var editorOpen by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var courseMenuExpanded by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val nameError = if (name.isBlank()) "Name is required" else null
    val emailError = when {
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        else -> null
    }
    val courseError = if (course.isBlank()) "Course is required" else null
    val ageValue = age.toIntOrNull()
    val ageError = if (age.isNotBlank() && (ageValue == null || ageValue <= 0)) {
        "Enter a positive age"
    } else null

    val openAdd = {
        editingStudent = null
        name = ""
        email = ""
        course = ""
        age = ""
        showValidationErrors = false
        editorOpen = true
    }
    val openEdit: (Student) -> Unit = { student ->
        editingStudent = student
        name = student.name
        email = student.email
        course = student.course
        age = if (student.age == 0) "" else student.age.toString()
        showValidationErrors = false
        editorOpen = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.width(maxWidth.coerceAtMost(760.dp)).fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DIRECTORY", style = MaterialTheme.typography.labelMedium, color = MutedInk)
                        Text("Students", style = MaterialTheme.typography.headlineMedium)
                    }
                    Button(
                        onClick = openAdd,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Add")
                    }
                }
            }

            item { StudentCountPanel(studentCount) }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::searchStudents,
                    label = { Text("Search students") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { viewModel.searchStudents("") }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                            }
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = Charcoal,
                        unfocusedBorderColor = SoftOutline
                    )
                )
            }

            item {
                Box {
                    OutlinedButton(
                        onClick = { courseMenuExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SoftOutline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceWhite,
                            contentColor = Charcoal
                        )
                    ) {
                        Icon(Icons.Outlined.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(selectedCourse ?: "All Courses")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = courseMenuExpanded,
                        onDismissRequest = { courseMenuExpanded = false },
                        containerColor = SurfaceWhite
                    ) {
                        (listOf<String?>(null) + courses).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option ?: "All Courses") },
                                onClick = {
                                    viewModel.filterByCourse(option)
                                    courseMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Student list", style = MaterialTheme.typography.titleMedium)
                    Text("${students.size} shown", style = MaterialTheme.typography.labelMedium, color = MutedInk)
                }
            }

            if (students.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Groups, contentDescription = null, tint = MutedInk, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No students found", style = MaterialTheme.typography.titleMedium)
                        if (searchQuery.isNotBlank() || selectedCourse != null) {
                            TextButton(onClick = {
                                viewModel.searchStudents("")
                                viewModel.filterByCourse(null)
                            }) { Text("Clear filters") }
                        } else {
                            TextButton(onClick = openAdd) { Text("Add student") }
                        }
                    }
                }
            }

            items(students, key = { it.id }) { student ->
                StudentRow(
                    student = student,
                    onEdit = { openEdit(student) },
                    onDelete = { viewModel.deleteStudent(student) }
                )
            }
        }
    }

    if (editorOpen) {
        ModalBottomSheet(
            onDismissRequest = { editorOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (editingStudent == null) "Add student" else "Edit student",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    IconButton(onClick = { editorOpen = false }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close editor")
                    }
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidationErrors && nameError != null,
                    supportingText = if (showValidationErrors && nameError != null) {
                        { Text(nameError) }
                    } else null
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidationErrors && emailError != null,
                    supportingText = if (showValidationErrors && emailError != null) {
                        { Text(emailError) }
                    } else null
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Course") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidationErrors && courseError != null,
                    supportingText = if (showValidationErrors && courseError != null) {
                        { Text(courseError) }
                    } else null
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidationErrors && ageError != null,
                    supportingText = if (showValidationErrors && ageError != null) {
                        { Text(ageError) }
                    } else null
                )
                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        showValidationErrors = true
                        if (nameError == null && emailError == null && courseError == null && ageError == null) {
                            val studentToEdit = editingStudent
                            if (studentToEdit == null) {
                                viewModel.addStudent(name, email, course, ageValue ?: 0)
                            } else {
                                viewModel.updateStudent(studentToEdit.copy(
                                    name = name,
                                    email = email,
                                    course = course,
                                    age = ageValue ?: 0
                                ))
                            }
                            editorOpen = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (editingStudent == null) "Add student" else "Save changes")
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun StudentCountPanel(count: Int) {
    Surface(
        color = Charcoal,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("TOTAL STUDENTS", style = MaterialTheme.typography.labelMedium, color = Color(0xFFC9D1CE))
                Spacer(Modifier.height(3.dp))
                Text("$count", fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF394244), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
private fun StudentRow(student: Student, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        color = SurfaceWhite,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PaleSage, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        student.name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Charcoal
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(student.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(student.email, style = MaterialTheme.typography.bodyMedium, color = MutedInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit ${student.name}", tint = Charcoal)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete ${student.name}", tint = MutedInk)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SoftOutline)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.School, contentDescription = null, modifier = Modifier.size(16.dp), tint = MutedInk)
                Spacer(Modifier.width(6.dp))
                Text(student.course, style = MaterialTheme.typography.labelMedium, color = MutedInk, modifier = Modifier.weight(1f))
                Text(
                    if (student.age == 0) "Age unknown" else "Age ${student.age}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedInk
                )
            }
        }
    }
}
