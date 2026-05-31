package com.iteam.buget.core.category;

import com.iteam.buget.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsDefaultTrue();
    List<Category> findByCreatedBy(User user);

    @Query("SELECT c FROM Category c WHERE c.isDefault = true OR c.createdBy = :user")
    List<Category> findAllAvailableForUser(User user);
}