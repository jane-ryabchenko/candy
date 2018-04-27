package org.candy.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Candy comparison report entity.
 */
@Getter
@Setter
@Entity
@Table(name = "reports")
public class ComparisonReport extends BaseUUIDEntity {
  @Column(name = "failed_comparisons")
  private int failedComparisons;

  @Column(name = "total_comparisons")
  private int totalComparisons;

  @Column(name = "upload_complete")
  private boolean uploadComplete;

  @OneToMany(mappedBy = "report")
  private List<Comparison> comparisonList;
}
