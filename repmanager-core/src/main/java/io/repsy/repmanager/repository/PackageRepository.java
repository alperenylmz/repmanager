package io.repsy.repmanager.repository;

import io.repsy.repmanager.model.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
    Optional<PackageEntity> findByNameAndVersion(String name, String version);
}
