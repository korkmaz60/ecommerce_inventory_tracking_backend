package com.ecommerce.ecommerce.product.repository;

import com.ecommerce.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Slug ile kategori bulma
    Optional<Category> findBySlug(String slug);

    // İsme göre kategori bulma
    Optional<Category> findByName(String name);

    // Aktif kategorileri getir
    List<Category> findByActiveTrue();

    // Ana kategorileri getir (parent'ı olmayan)
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
    List<Category> findRootCategories();

    // Belirli bir parent'a ait alt kategorileri getir
    List<Category> findByParentIdAndActiveTrue(Long parentId);

    // Kategori varlık kontrolü
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
