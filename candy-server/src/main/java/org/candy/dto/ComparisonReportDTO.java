package org.candy.dto;

import lombok.Getter;
import lombok.Setter;
import org.candy.data.entity.ComparisonReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link org.candy.data.entity.ComparisonReport}.
 */
@Getter
@Setter
public class ComparisonReportDTO implements Serializable {
  private boolean uploadComplete;

  private int failedComparisons;

  private int totalComparisons;

  private List<ComparisonDTO> comparisonList;

  public ComparisonReportDTO() {}

  public ComparisonReportDTO(ComparisonReport report) {
    uploadComplete = report.isUploadComplete();
    failedComparisons = report.getFailedComparisons();
    totalComparisons = report.getTotalComparisons();
    comparisonList = new ArrayList<>(
        report.getComparisonList().stream().map(ComparisonDTO::new).collect(Collectors.toList()));
  }
}
