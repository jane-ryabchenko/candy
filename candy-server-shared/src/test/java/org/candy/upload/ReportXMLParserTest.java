package org.candy.upload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportXMLParserTest {
  @Mock private SAXParser parser;
  private ReportXMLParser reportXMLParser;

  private String[][] testData = new String[][]{
      new String[]{"report", null, null},
      new String[]{"test-class", "org.candy.class1.Class", "test-class"},
      new String[]{"test-method", "testFoo", "test-method"},
      new String[]{"comparison-list", null, null},
      new String[]{"comparison", null, null},
      new String[]{"diff-percentage", "1.0", "diff-percentage"},
      new String[]{"actual-file-name", "actual-file-name1", "actual-file-name"},
      new String[]{"actual-file-info", "actual-file-info1", "actual-file-info"},
      new String[]{"origin-file-name", "origin-file-name1", "origin-file-name"},
      new String[]{"origin-file-info", "origin-file-info1", "origin-file-info"},
      new String[]{null, null, "comparison"},
      new String[]{"comparison", null, null},
      new String[]{"diff-percentage", "2.0", "diff-percentage"},
      new String[]{"actual-file-name", "actual-file-name2", "actual-file-name"},
      new String[]{"actual-file-info", "actual-file-info2", "actual-file-info"},
      new String[]{"origin-file-name", "origin-file-name2", "origin-file-name"},
      new String[]{"origin-file-info", "origin-file-info2", "origin-file-info"},
      new String[]{null, null, "comparison"},
      new String[]{null, null, "comparison-list"},
      new String[]{null, null, "report"},
  };

  @Before
  public void before() throws IOException, SAXException, ParserConfigurationException {
    reportXMLParser = spy(new ReportXMLParser());
    Mockito.doAnswer(invocationOnMock -> {
      DefaultHandler handler = invocationOnMock.getArgumentAt(1, DefaultHandler.class);
      for (String[] strList : testData) {
        if (strList[0] != null) {
          handler.startElement(null, null, strList[0], null);
        }
        if (strList[1] != null) {
          handler.characters(strList[1].toCharArray(), 0, strList[1].length());
        }
        if (strList[2] != null) {
          handler.endElement(null, null, strList[2]);
        }
      }
      return null;
    }).when(parser).parse(Matchers.any(File.class), Matchers.any(DefaultHandler.class));
    when(reportXMLParser.newSAXParser()).thenReturn(parser);
  }

  @Test
  public void testReportXMLParser() {
    List<ComparisonDTO> listComparisons;
    listComparisons = reportXMLParser.parseReportXML(new File(""));

    assertThat(listComparisons.size()).isEqualTo(2);

    ComparisonDTO dto = listComparisons.get(0);
    assertThat(dto.getTestClass()).isEqualTo("org.candy.class1.Class");
    assertThat(dto.getTestMethod()).isEqualTo("testFoo");
    assertThat(dto.getDiffPercentage()).isEqualTo(1.0);
    assertThat(dto.getActualFileName())
        .isEqualTo("actual-file-name1");
    assertThat(dto.getActualFileInfo()).isEqualTo("actual-file-info1");
    assertThat(dto.getOriginFileName())
        .isEqualTo("origin-file-name1");
    assertThat(dto.getOriginFileInfo()).isEqualTo("origin-file-info1");


    dto = listComparisons.get(1);
    assertThat(dto.getTestClass()).isEqualTo("org.candy.class1.Class");
    assertThat(dto.getTestMethod()).isEqualTo("testFoo");
    assertThat(dto.getDiffPercentage()).isEqualTo(2.0);
    assertThat(dto.getActualFileName())
        .isEqualTo("actual-file-name2");
    assertThat(dto.getActualFileInfo()).isEqualTo("actual-file-info2");
    assertThat(dto.getOriginFileName())
        .isEqualTo("origin-file-name2");
    assertThat(dto.getOriginFileInfo()).isEqualTo("origin-file-info2");
  }
}
