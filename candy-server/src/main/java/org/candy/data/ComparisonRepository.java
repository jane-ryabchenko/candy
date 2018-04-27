package org.candy.data;

import org.candy.data.entity.Comparison;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for {@link Comparison} entity.
 */
@Repository
public interface ComparisonRepository extends CrudRepository<Comparison, String> {}
