package com.example.hotelswebapp.repos;

import com.example.hotelswebapp.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long> {
    List<Service> findAll();
}
