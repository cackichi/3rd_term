package com.example.hotelswebapp.services;

import com.example.hotelswebapp.repos.ServiceRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceOfServices {
    private final ServiceRepo serviceRepo;

    public List<com.example.hotelswebapp.entity.Service> findAll(){
        return serviceRepo.findAll();
    }
}
