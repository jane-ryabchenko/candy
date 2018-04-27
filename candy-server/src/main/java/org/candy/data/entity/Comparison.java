package org.candy.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Candy comparison entity.
 */
@Getter
@Setter
@Entity
@Table(name = "comparisons")
public class Comparison extends BaseEntity {
  @Column(name = "test_class", nullable = false)
  private String testClass;

  @Column(name = "test_method", nullable = false)
  private String testMethod;

  @Column(name = "diff_percentage", nullable = false)
  private double diffPercentage;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "origin_image", unique= true, updatable = false)
  private Image originImage;

  @Column(name = "origin_file_name", nullable = false)
  private String originFileName;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "actual_image", unique= true, nullable = false, updatable = false)
  private Image actualImage;

  @Column(name = "actual_file_name", nullable = false)
  private String actualFileName;

  @ManyToOne
  @JoinColumn(name = "report_id", nullable = false, updatable = false)
  private ComparisonReport report;
}
