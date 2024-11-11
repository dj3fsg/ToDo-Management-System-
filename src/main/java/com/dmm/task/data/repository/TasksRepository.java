package com.dmm.task.data.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.LinkedMultiValueMap;

import com.dmm.task.data.entity.Tasks;
public interface TasksRepository extends JpaRepository<Tasks, Integer> {

	@Query("select a from Tasks a where a.date between :from and :to and name = :name")
	LinkedMultiValueMap<LocalDate, String> findByDateBetween(@Param("from") LocalDate this1stDay, @Param("to") LocalDate currentDate, @Param("name") String name);
}
