package org.candy.report;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.candy.generic.comparison.ImageDiff;
import org.junit.runner.Description;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestReport {
  private Document document;
  private Element comparisonList;

  public TestReport(Description description) {
    try {
      document = createDocument();

      Element rootElement = createNode(document, "report");

      createNode(rootElement, "test-class", description.getClassName());
      createNode(rootElement, "test-method", description.getMethodName());

      comparisonList = createNode(rootElement, "comparison-list");
    } catch(ParserConfigurationException ex) {
      ex.printStackTrace();
    }
  }

  public void addComparison(ImageDiff diff) {
    Element comparison = createNode(comparisonList, "comparison");
    createNode(comparison, "diff-percentage", String.format("%2.2f", diff.getDiffPercentage()));
    createNode(comparison, "actual-file-name", diff.getActualFileName());
    createNode(comparison, "actual-file-info", diff.getActualInfo());
    createNode(comparison, "origin-file-name", diff.getOriginFileName());
    createNode(comparison, "origin-file-info", diff.getOriginInfo());
  }

  public void writeReport(File outputFile) {
    try {
      StreamResult result = new StreamResult(outputFile);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(new DOMSource(document), result);
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }

  private Document createDocument() throws ParserConfigurationException {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    return builder.newDocument();
  }

  private Element createNode(Node parent, String nodeName) {
    Element node = document.createElement(nodeName);
    parent.appendChild(node);
    return node;
  }

  private Element createNode(Node parent, String nodeName, String text) {
    Element node = document.createElement(nodeName);
    node.appendChild(document.createTextNode(text));
    parent.appendChild(node);
    return node;
  }
}
