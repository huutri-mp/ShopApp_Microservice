package com.example.productservice.repository;

import com.example.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
                                            JpaSpecificationExecutor<Category> {
    List<Category> findByParentIsNull();
    boolean existsByParent_Id(Long parentId);
    boolean existsByName (String name);
    Optional<Category> findBySlug(String slug);
    boolean existsByNameAndIdNot(String name, Long id);
}
