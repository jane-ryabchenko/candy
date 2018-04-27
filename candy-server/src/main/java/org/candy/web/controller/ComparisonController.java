package org.candy.web.controller;

import org.candy.dto.ComparisonDTO;
import org.candy.generic.CandyException;
import org.candy.service.ComparisonReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * Candy comparison controller.
 */
@RestController
public class ComparisonController {
  private ComparisonReportService reportService;

  @Inject
  public ComparisonController(ComparisonReportService reportService) {
    this.reportService = reportService;
  }

  @GetMapping(path = "/api/images/{imageId}", produces = IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getScreenshot(@PathVariable("imageId") String imageId) {
    Optional<byte[]> screenshot = reportService.getImage(imageId);
    if (screenshot.isPresent()) {
      return new ResponseEntity<>(screenshot.get(), HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PostMapping(
      path = "/api/reports/{reportId}/comparisons",
      headers = {"content-type=multipart/mixed","content-type=multipart/form-data"})
  @ResponseStatus(HttpStatus.CREATED)
  public void createComparison(
      @PathVariable("reportId") String reportId,
      @RequestPart(value = "comparison") ComparisonDTO newComparison,
      @RequestPart(value = "actual") MultipartFile actualFile,
      @RequestPart(value = "origin") MultipartFile originFile) {
    try {
      newComparison.setActualImage(actualFile.getBytes());
      newComparison.setOriginImage(originFile.getBytes());
      reportService.createComparison(reportId, newComparison);
    } catch (IOException ex) {
      throw new CandyException("Can't save image.", ex);
    }
  }
}
