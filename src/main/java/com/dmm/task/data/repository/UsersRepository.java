package com.dmm.task.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Users;
public interface UsersRepository extends JpaRepository<Users, String> {

}