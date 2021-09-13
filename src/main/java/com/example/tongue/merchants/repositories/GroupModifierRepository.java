package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.GroupModifier;
import com.example.tongue.merchants.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupModifierRepository extends JpaRepository<GroupModifier,Long> {
    Page<GroupModifier> findAll(Pageable pageable);
    List<GroupModifier> findAllByProduct_Id(Long Id);
}
