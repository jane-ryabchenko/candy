package org.candy.upload;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportUploaderTest {
  private static final String REPORT_ID = "reportId";
  private static final String UPLOAD_URL = "uploadUrl";

  @Mock private CloseableHttpClient httpClient;
  @Mock private CloseableHttpResponse response1;
  @Mock private StatusLine status1;
  @Mock private CloseableHttpResponse response2;
  @Mock private StatusLine status2;
  @Mock private HttpEntity entity;

  private ReportUploader uploader;

  @Before
  public void before() throws IOException {
    uploader =
        spy(new ReportUploader("src/test/resources/candy-actual", UPLOAD_URL, new File("."), httpClient, new Gson()));

    when(httpClient.execute(any(HttpPost.class))).thenReturn(response1).thenReturn(response2);

    when(response1.getStatusLine()).thenReturn(status1);
    when(status1.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(response1.getEntity()).thenReturn(entity);
    when(entity.getContent()).thenReturn(new ByteArrayInputStream(REPORT_ID.getBytes()));

    when(response2.getStatusLine()).thenReturn(status2);
    when(status2.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
  }

  @Test
  public void testUpload() throws IOException {
    String link = uploader.uploadReport();
    assertThat(link).isEqualTo(UPLOAD_URL + "/" + REPORT_ID);

    ArgumentCaptor<ComparisonDTO> argument = ArgumentCaptor.forClass(ComparisonDTO.class);
    verify(uploader, times(3)).uploadComparison(eq(REPORT_ID), argument.capture());
    ComparisonDTO dto = argument.getAllValues().get(0);
    assertThat(dto.getComparisonNumber()).isEqualTo(0);
    assertThat(dto.getTestClass()).isEqualTo("org.candy.integration.ScreenshotCaptorIntegrationTest");
    assertThat(dto.getTestMethod()).isEqualTo("testCaptureFailingScreenshot");
    assertThat(dto.getDiffPercentage()).isWithin(0.0).of(79.38);
    assertThat(dto.getActualFileName())
        .isEqualTo("src/test/resources/candy-actual/testCaptureFailingScreenshot_capturedBodyScreenshot.png");
    assertThat(dto.getActualFileInfo()).isEqualTo("1264x317");
    assertThat(dto.getOriginFileName())
        .isEqualTo("src/test/resources/candy-origin/testCaptureFailingScreenshot_capturedBodyScreenshot.png");
    assertThat(dto.getOriginFileInfo()).isEqualTo("2770x635");

    dto = argument.getAllValues().get(1);
    assertThat(dto.getComparisonNumber()).isEqualTo(1);
    assertThat(dto.getTestClass()).isEqualTo("org.candy.integration.ScreenshotCaptorIntegrationTest");
    assertThat(dto.getTestMethod()).isEqualTo("testCaptureScreenshot");
    assertThat(dto.getDiffPercentage()).isWithin(0.0).of(79.1);
    assertThat(dto.getActualFileName())
        .isEqualTo("src/test/resources/candy-actual/testCaptureScreenshot_capturedBodyScreenshotWithExclusion.png");
    assertThat(dto.getActualFileInfo()).isEqualTo("1264x317");
    assertThat(dto.getOriginFileName())
        .isEqualTo("src/test/resources/candy-origin/testCaptureScreenshot_capturedBodyScreenshotWithExclusion.png");
    assertThat(dto.getOriginFileInfo()).isEqualTo("2770x635");

    dto = argument.getAllValues().get(2);
    assertThat(dto.getComparisonNumber()).isEqualTo(2);
    assertThat(dto.getTestClass()).isEqualTo("org.candy.integration.ScreenshotCaptorIntegrationTest");
    assertThat(dto.getTestMethod()).isEqualTo("testCaptureScreenshot");
    assertThat(dto.getDiffPercentage()).isWithin(0.0).of(78.57);
    assertThat(dto.getActualFileName())
        .isEqualTo("src/test/resources/candy-actual/testCaptureScreenshot_capturedBodyScreenshotWithExclusion2.png");
    assertThat(dto.getActualFileInfo()).isEqualTo("1264x317");
    assertThat(dto.getOriginFileName())
        .isEqualTo("src/test/resources/candy-origin/testCaptureScreenshot_capturedBodyScreenshotWithExclusion2.png");
    assertThat(dto.getOriginFileInfo()).isEqualTo("2770x635");
  }
}
