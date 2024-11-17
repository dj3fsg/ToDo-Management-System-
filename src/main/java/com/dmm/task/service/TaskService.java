package com.dmm.task.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;

@Service
public class TaskService {
	
	 private final TasksRepository repo;

	    @Autowired
	    public TaskService(TasksRepository repo) {
	        this.repo = repo;
	    }
	    
	    @Transactional
	    public Tasks save(Tasks tasks) {
	        return repo.save(tasks);
	    }
	    
	    
	    @Transactional
	    public Optional<Tasks> getTaskById(Integer id) {
	        return repo.findById(id);
	    }
	    
	

}
