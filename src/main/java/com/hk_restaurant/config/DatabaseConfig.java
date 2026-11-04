package com.hk_restaurant.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;
import java.util.HashMap;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Chua co DB_PASSWORD. Hay dat mat khau database Render trong bien moi truong nay.");
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", password);
        properties.put("hibernate.show_sql", "false");
        override(properties, "DB_URL", "jakarta.persistence.jdbc.url");
        override(properties, "DB_USER", "jakarta.persistence.jdbc.user");
        return Persistence.createEntityManagerFactory("restaurantPU", properties);
    }

    private static void override(Map<String, Object> properties, String env, String key) {
        String value = System.getenv(env);
        if (value != null && !value.isBlank()) properties.put(key, value);
    }
}
