
package com.hk_restaurant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.hk_restaurant.config.DatabaseConfig;

public class TestConnection {

    public static void main(String[] args) {

        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("Chưa có DB_PASSWORD!");
            System.exit(1);
        }

        try (EntityManagerFactory emf =
                     DatabaseConfig.createEntityManagerFactory()) {

            EntityManager em = emf.createEntityManager();

            try {
                Object result = em.createNativeQuery(
                        "SELECT current_database()"
                ).getSingleResult();

                System.out.println(
                        "KET NOI THANH CONG: " + result
                );

            } finally {
                em.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
