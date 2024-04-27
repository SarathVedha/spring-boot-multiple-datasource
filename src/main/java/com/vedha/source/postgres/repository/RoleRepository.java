package com.vedha.source.postgres.repository;

import com.vedha.source.postgres.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
