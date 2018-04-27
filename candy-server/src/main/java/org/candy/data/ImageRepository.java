package org.candy.data;

/**
 *
 */

import org.candy.data.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for {@link Image} entity.
 */
@Repository
public interface ImageRepository extends CrudRepository<Image, String> {}
