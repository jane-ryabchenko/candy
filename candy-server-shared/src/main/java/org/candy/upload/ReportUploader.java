package org.candy.upload;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

public class ReportUploader {
  private String testReportFolder;
  private String uploadURL;
  private File baseDir;
  private CloseableHttpClient httpClient;
  private Gson gson;

  public ReportUploader(String testReportFolder, String uploadURL, File baseDir) {
    this(testReportFolder, uploadURL, baseDir, HttpClientBuilder.create().build(), new Gson());
  }

  @VisibleForTesting
  ReportUploader(
      String testReportFolder, String uploadURL, File baseDir, CloseableHttpClient httpClient, Gson gson) {
    this.testReportFolder = checkNotNull(testReportFolder);
    this.uploadURL = checkNotNull(uploadURL);
    this.baseDir = checkNotNull(baseDir);
    this.httpClient = checkNotNull(httpClient);
    this.gson = checkNotNull(gson);
  }

  public String uploadReport() {
    File reportFolder = openFile(testReportFolder);
    if (!reportFolder.exists()) {
      throw new IllegalArgumentException("Report folder does not exist: " + reportFolder.getAbsolutePath());
    }
    File[] reportFiles = reportFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
    if (reportFiles == null || reportFiles.length == 0) {
      throw new IllegalStateException("Report folder does not have XML files. Nothing to upload.");
    }
    List<ComparisonDTO> comparisons = new ArrayList<>();
    ReportXMLParser reportXMLParser = new ReportXMLParser();
    for (File reportFile : reportFiles) {
      comparisons.addAll(reportXMLParser.parseReportXML(reportFile));
    }
    try {
      return uploadReport(comparisons);
    } catch (IOException ex) {
      throw new RuntimeException("Can not upload test results.", ex);
    }
  }

  private String uploadReport(List<ComparisonDTO> comparisons) throws IOException {
    HttpEntity entity = new StringEntity("{\"totalComparisons\":\"" + comparisons.size() + "\"}", APPLICATION_JSON);
    HttpPost post = new HttpPost(uploadURL + "/api/reports");
    post.setEntity(entity);
    CloseableHttpResponse response = httpClient.execute(post);
    int status = response.getStatusLine().getStatusCode();
    if (status != HttpStatus.SC_OK) {
      throw new RuntimeException("Unsuccessful upload of report. Status " + status + "\n"
          + response.getStatusLine().getReasonPhrase());
    }
    String reportId = EntityUtils.toString(response.getEntity(), "UTF-8");
    response.close();
    int number = 0;
    for (ComparisonDTO comparison : comparisons) {
      comparison.setComparisonNumber(number++);
      uploadComparison(reportId, comparison);
    }
    return uploadURL + "/" + reportId;
  }

  @VisibleForTesting
  void uploadComparison(String reportId, ComparisonDTO comparison) throws IOException {
    File actualFile = openFile(comparison.getActualFileName());
    if (!actualFile.exists()) {
      throw new IllegalArgumentException("Actual file does not exist: " + actualFile.getAbsolutePath());
    }
    File originFile = openFile(comparison.getOriginFileName());
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
        .create()
        .addTextBody("comparison", gson.toJson(comparison), APPLICATION_JSON)
        .addBinaryBody("actual", actualFile, IMAGE_PNG, "actual");
    if (originFile.exists()) {
      entityBuilder.addBinaryBody("origin", originFile, IMAGE_PNG, "origin");
    }
    HttpPost post = new HttpPost(uploadURL + "/api/reports/" + reportId + "/comparisons");
    post.setEntity(entityBuilder.build());
    try (CloseableHttpResponse response = httpClient.execute(post)) {
      int status = response.getStatusLine().getStatusCode();
      if (status != HttpStatus.SC_CREATED) {
        throw new RuntimeException("Unsuccessful upload of comparison. Status " + status + "\n"
            + EntityUtils.toString(response.getEntity(), "UTF-8"));
      }
    }
  }

  private File openFile(String fileName) {
    return new File(baseDir, fileName);
  }
}
