package org.candy.service.impl;

import java.util.Optional;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.candy.data.ComparisonReportRepository;
import org.candy.data.ComparisonRepository;
import org.candy.data.ImageRepository;
import org.candy.data.entity.Comparison;
import org.candy.data.entity.ComparisonReport;
import org.candy.data.entity.Image;
import org.candy.dto.ComparisonDTO;
import org.candy.dto.ComparisonReportDTO;
import org.candy.generic.CandyException;
import org.candy.service.ComparisonReportService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import static com.google.common.base.Preconditions.checkState;

/**
 * Implementation of for {@link ComparisonReportService}.
 */
@Service
@Qualifier("defaultComparisonReportService")
public class ComparisonReportServiceImpl implements ComparisonReportService {
  private ComparisonReportRepository comparisonReportRepository;

  private ComparisonRepository comparisonRepository;

  private ImageRepository imageRepository;

  @Inject
  public ComparisonReportServiceImpl(
      ComparisonReportRepository comparisonReportRepository,
      ComparisonRepository comparisonRepository,
      ImageRepository imageRepository) {
    this.comparisonReportRepository = comparisonReportRepository;
    this.comparisonRepository = comparisonRepository;
    this.imageRepository = imageRepository;
  }

  @Override
  @Transactional
  public ComparisonReportDTO getReport(String reportId) {
    ComparisonReport report = getReportEntity(reportId);
    Hibernate.initialize(report);
    return new ComparisonReportDTO(report);
  }

  @Override
  public Optional<byte[]> getImage(String imageId) {
    Optional<Image> image = imageRepository.findById(imageId);
    return image.map(Image::getData);
  }

  @Override
  @Transactional
  public String createReport(ComparisonReportDTO newComparisonReport) {
    ComparisonReport report = new ComparisonReport();
    report.setFailedComparisons(newComparisonReport.getFailedComparisons());
    report.setTotalComparisons(newComparisonReport.getTotalComparisons());
    report = comparisonReportRepository.save(report);
    return report.getId();
  }

  @Override
  @Transactional
  public void createComparison(String reportId, ComparisonDTO newComparison) {
    ComparisonReport report = getReportEntity(reportId);
    String comparisonId = reportId + "_" + newComparison.getComparisonNumber();
    checkState(
        report.getComparisonList().stream().noneMatch((c) -> comparisonId.equals(c.getId())),
        "Comparison with id " + comparisonId + " already exist.");
    Comparison comparison = new Comparison();
    comparison.setId(comparisonId);
    comparison.setTestClass(newComparison.getTestClass());
    comparison.setTestMethod(newComparison.getTestMethod());
    comparison.setDiffPercentage(newComparison.getDiffPercentage());
    comparison.setOriginFileName(newComparison.getOriginFileName());
    comparison.setOriginImage(createImageEntity(comparisonId, "origin", newComparison.getOriginImage()));
    comparison.setActualFileName(newComparison.getActualFileName());
    comparison.setActualImage(createImageEntity(comparisonId, "actual", newComparison.getActualImage()));
    comparison.setReport(report);
    comparisonRepository.save(comparison);
    if (report.getComparisonList().size() == report.getTotalComparisons() - 1) {
      report.setUploadComplete(true);
      comparisonReportRepository.save(report);
    }
  }

  private ComparisonReport getReportEntity(String reportId) {
    Optional<ComparisonReport> report = comparisonReportRepository.findById(reportId);
    if (!report.isPresent()) {
      throw new CandyException("There's no report with id@" + reportId);
    }

    return report.get();
  }

  private Image createImageEntity(String comparisonId, String imageType, byte[] data) {
    if (data == null) {
      return null;
    }
    Image image = new Image();
    image.setId(comparisonId + "_" + imageType);
    image.setData(data);
    return image;
  }
}
