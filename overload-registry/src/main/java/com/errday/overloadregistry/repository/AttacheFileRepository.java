package com.errday.overloadregistry.repository;

import com.errday.overloadregistry.entity.AttacheFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttacheFileRepository extends JpaRepository<AttacheFile, Long> {
    Optional<AttacheFile> findBySaveFileName(String saveFileName);
    List<AttacheFile> findByLoadId(long loadId);
}
