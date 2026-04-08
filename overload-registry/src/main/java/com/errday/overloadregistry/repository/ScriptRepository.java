package com.errday.overloadregistry.repository;

import com.errday.overloadregistry.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    Optional<Script> findBySaveFileName(String saveFileName);
    Optional<Script> findByLoadId(Long loadId);
}
