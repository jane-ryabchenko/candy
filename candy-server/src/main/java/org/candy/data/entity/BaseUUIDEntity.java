package org.candy.data.entity;

import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

/**
 * Base entity with UUID String key.
 */
@MappedSuperclass
public abstract class BaseUUIDEntity extends BaseEntity {
  public BaseUUIDEntity() {
    setId(UUID.randomUUID().toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseUUIDEntity that = (BaseUUIDEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
