package org.candy.service;

import org.candy.data.entity.Comparison;
import org.candy.data.entity.ComparisonReport;
import org.candy.data.entity.Image;
import org.candy.dto.ComparisonDTO;
import org.candy.dto.ComparisonReportDTO;

import java.util.Optional;

/**
 * Service for {@link ComparisonReport} and {@link org.candy.data.entity.Comparison}.
 */
public interface ComparisonReportService {
  /**
   * Returns DTO representation of {@link ComparisonReport} with given ID.
   * @param reportId id of {@link ComparisonReport}
   * @return {@link ComparisonReportDTO}
   */
  ComparisonReportDTO getReport(String reportId);

  /**
   * Returns content of {@link Image} with given ID.
   * @param imageId id of {@link Image}
   * @return {@code Optional<byte[]>}
   */
  Optional<byte[]> getImage(String imageId);

  /**
   * Creates new {@link ComparisonReport} and returns its ID.
   * @param newComparisonReport DTO with two numeric values
   * @return {@link ComparisonReport#id}
   */
  String createReport(ComparisonReportDTO newComparisonReport);

  /**
   * Creates new {@link Comparison}.
   * @param newComparison DTO with comparison data
   */
  void createComparison(String reportId, ComparisonDTO newComparison);
}
