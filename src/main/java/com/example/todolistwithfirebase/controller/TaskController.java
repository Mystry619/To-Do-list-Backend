package com.example.todolistwithfirebase.controller;

import com.example.todolistwithfirebase.model.Task;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class TaskController {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    @PostMapping("/create/{email}")
    public ResponseEntity<String> createTask(@RequestBody Task newTask, @PathVariable String email) {
        try {
            String userEmail = email;

            DatabaseReference userTasksRef = firebaseDatabase.getReference("Users").child(userEmail.replace('.', ','))
                    .child("Tasks");

            DatabaseReference newTaskRef = userTasksRef.push();


            String taskId = newTaskRef.getKey();

            newTask.setId(taskId);

            newTaskRef.setValueAsync(newTask);

            return ResponseEntity.ok("Task created successfully with ID: " + taskId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create task: " + e.getMessage());
        }
    }




    // Update Task
    @PutMapping("/tasks/{email}/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable String email, @PathVariable String taskId, @RequestBody Task updatedTask) {
        try {

            String userEmail = email;

            DatabaseReference tasksRef = firebaseDatabase.getReference("Users").child(userEmail.replace('.', ','))
                    .child("Tasks");


            DatabaseReference taskRef = tasksRef.child(taskId);

            updatedTask.setId(taskId);


            taskRef.setValueAsync(updatedTask);



            return ResponseEntity.ok("Task updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update task: " + e.getMessage());
        }
    }


    @DeleteMapping("/tasks/{email}/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String email,@PathVariable String taskId) {
        try {
            String userEmail = email;

            DatabaseReference tasksRef = firebaseDatabase.getReference("Users").child(userEmail.replace('.', ','))
                    .child("Tasks");

            DatabaseReference taskRef = tasksRef.child(taskId);

            taskRef.removeValueAsync();

            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete task: " + e.getMessage());
        }
    }




        @GetMapping("/tasks/{email}")
        public ResponseEntity<List<Task>> getAllTasksForUser(@PathVariable String email) {
            try {
                DatabaseReference tasksRef = firebaseDatabase.getReference("Users");

                DatabaseReference userTasksRef = tasksRef.child(email.replace('.', ','))
                        .child("Tasks");
                System.out.println(userTasksRef);

                List<Task> tasks = retrieveTasks(userTasksRef);

                System.out.println(tasks);

                return ResponseEntity.ok(tasks);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        private List<Task> retrieveTasks(DatabaseReference ref) throws InterruptedException, ExecutionException {
            CompletableFuture<List<Task>> futureTasks = new CompletableFuture<>();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Task> tasks = new ArrayList<>();
                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        Task task = taskSnapshot.getValue(Task.class);
                        tasks.add(task);
                    }
                    futureTasks.complete(tasks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    futureTasks.completeExceptionally(databaseError.toException());
                }
            });
            return futureTasks.get();
        }




        @GetMapping("/tasks/{email}/completed")
        public ResponseEntity<List<Task>> getCompletedTasksForUser(@PathVariable String email) {
            try {
                DatabaseReference tasksRef = firebaseDatabase.getReference("Users");

                DatabaseReference userTasksRef = tasksRef.child(email.replace('.', ','))
                        .child("Tasks");

                Query completedTasksQuery = userTasksRef.orderByChild("completed").equalTo(true);

                List<Task> completedTasks = retrieveTasks(completedTasksQuery);

                return ResponseEntity.ok(completedTasks);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        @GetMapping("/tasks/{email}/incomplete")
        public ResponseEntity<List<Task>> getIncompleteTasksForUser(@PathVariable String email) {
            try {
                DatabaseReference tasksRef = firebaseDatabase.getReference("Users");

                DatabaseReference userTasksRef = tasksRef.child(email.replace('.', ','))
                        .child("Tasks");

                Query incompleteTasksQuery = userTasksRef.orderByChild("completed").equalTo(false);

                List<Task> incompleteTasks = retrieveTasks(incompleteTasksQuery);

                return ResponseEntity.ok(incompleteTasks);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        private List<Task> retrieveTasks(Query query) throws InterruptedException, ExecutionException {
            CompletableFuture<List<Task>> futureTasks = new CompletableFuture<>();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Task> tasks = new ArrayList<>();
                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        Task task = taskSnapshot.getValue(Task.class);
                        tasks.add(task);
                    }
                    futureTasks.complete(tasks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    futureTasks.completeExceptionally(databaseError.toException());
                }
            });
            return futureTasks.get();
        }
    }






