package com.example.contractmanagement.service;

import com.example.contractmanagement.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
	User createUser(User user);

	List<User> getAllUsers();

	Optional<User> getUserById(Long id);

	void deleteUser(Long id);
}
