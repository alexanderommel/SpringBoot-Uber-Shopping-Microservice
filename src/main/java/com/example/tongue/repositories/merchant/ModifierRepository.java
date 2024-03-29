package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.Modifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier,Long> {
    List<Modifier> findAllByGroupModifier_Id(Long id);
}
