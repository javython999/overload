package com.errday.overloadregistry.repository;

import com.errday.overloadregistry.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Summary findByLoadId(Long loadId);
}
