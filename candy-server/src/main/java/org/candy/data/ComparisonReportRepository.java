package org.candy.data;

import org.candy.data.entity.ComparisonReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for {@link ComparisonReport} entity.
 */
@Repository
public interface ComparisonReportRepository extends CrudRepository<ComparisonReport, String> {}
