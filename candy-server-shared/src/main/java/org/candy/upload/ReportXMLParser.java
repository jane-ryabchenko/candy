package org.candy.upload;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.candy.generic.CandyException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReportXMLParser {
  private class SAXHandler extends DefaultHandler {
    ComparisonDTO comparisonDTO;
    String value;
    String testClassName;
    String testMethodName;
    List<ComparisonDTO> listComparisons = new ArrayList<>();

    @Override
    public void characters(char[] buffer, int start, int length) {
      value = new String(buffer, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
      if (name.equalsIgnoreCase("comparison")) {
        comparisonDTO = new ComparisonDTO();
        listComparisons.add(comparisonDTO);
      }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
      switch (name.toLowerCase()) {
        case "test-class":
          testClassName = value;
          break;
        case "test-method":
          testMethodName = value;
          break;
        case "comparison":
          comparisonDTO.setTestClass(testClassName);
          comparisonDTO.setTestMethod(testMethodName);
          break;
        case "diff-percentage":
          comparisonDTO.setDiffPercentage(Double.parseDouble(value));
          break;
        case "actual-file-name":
          comparisonDTO.setActualFileName(value);
          break;
        case "actual-file-info":
          comparisonDTO.setActualFileInfo(value);
          break;
        case "origin-file-name":
          comparisonDTO.setOriginFileName(value);
          break;
        case "origin-file-info":
          comparisonDTO.setOriginFileInfo(value);
          break;
        case "report":
        case "comparison-list":
          break;
        default:
          throw new RuntimeException("Unsupported XML tag: " + name);
      }
    }
  }

  public List<ComparisonDTO> parseReportXML(File reportFile) {
    try {
      SAXParser parser = newSAXParser();
      SAXHandler handler = new SAXHandler();
      parser.parse(reportFile, handler);
      return handler.listComparisons;
    } catch (IOException | SAXException | ParserConfigurationException ex) {
      throw new CandyException("Can not parse report XML: " + reportFile.getAbsolutePath(), ex);
    }
  }

  @VisibleForTesting
  SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    return factory.newSAXParser();
  }
}
