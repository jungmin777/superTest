package com.example.ospe.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ospe.user.dto.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
   
   List<User> findByUsername(String username); 
   
   List<User> findByUsernameAndUseYn(String username,String useYn);
   
   List<User> findByUsernameAndPassword(String username, String password);
   
   boolean existsByUsername(String username);
   
   List<User> findByNameLike(String name);
   
   List<User> findByName(String name); //이름
   
   
}
