package org.candy.web.controller;

import org.candy.dto.ComparisonReportDTO;
import org.candy.service.ComparisonReportService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Candy comparison report controller.
 */
@RestController
@RequestMapping("/api/reports")
public class ComparisonReportController {
  private ComparisonReportService reportService;

  @Inject
  public ComparisonReportController(ComparisonReportService reportService) {
    this.reportService = reportService;
  }

  @GetMapping(path = "{reportId}", produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ComparisonReportDTO getReport(@PathVariable("reportId") String reportId) {
    return reportService.getReport(reportId);
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @ResponseBody
  public String createReport(@RequestBody ComparisonReportDTO newComparisonReport) {
    return reportService.createReport(newComparisonReport);
  }
}
