package com.codesoom.assignment.controllers;

import com.codesoom.assignment.exceptions.TaskNotFoundException;
import com.codesoom.assignment.exceptions.TaskTitleEmptyException;
import com.codesoom.assignment.models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final Map<Long, Task> tasks = new HashMap<>();
    private Long newTaskId = 0L;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Task> list() {
        return new ArrayList<>(tasks.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable("id") final Long id) {
        final var task = tasks.get(id);
        Optional.ofNullable(task)
                .orElseThrow(TaskNotFoundException::new);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        if (task.getTitle().isBlank()) {
            throw new TaskTitleEmptyException();
        }
        newTaskId += 1;
        task.setId(newTaskId);
        tasks.put(task.getId(), task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable("id") final Long id, @RequestBody final Task newTaskForUpdate) {
        if (newTaskForUpdate.getTitle().isBlank()) {
            logger.debug("task={}", newTaskForUpdate.getTitle());
            throw new TaskTitleEmptyException();
        }

        var originalTask = tasks.get(id);
        Optional.ofNullable(originalTask)
                .orElseThrow(TaskNotFoundException::new);

        originalTask.setTitle(newTaskForUpdate.getTitle());
        return new ResponseEntity<>(originalTask, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") final Long id) {
        final var deletedTask = tasks.remove(id);
        Optional.ofNullable(deletedTask)
                .orElseThrow(TaskNotFoundException::new);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
