package com.newapi.analyzer.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;


@Service
public class DatabaseService {

    private final EntityManager entityManager;

    public DatabaseService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isConnected() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
