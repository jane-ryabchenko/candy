package org.candy.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Image entity.
 */
@Getter
@Setter
@Entity
@Table(name = "images")
public class Image extends BaseEntity {
  @Lob
  @Column(name = "data", nullable = false, updatable = false)
  private byte[] data;
}
