package config.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class XMLFileReader {

    // Method to read the XML file
    public static String readXmlFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    // Method to replace placeholder with actual value
    public static String replacePlaceholder(String xmlContent, String placeholder, String value) {
        return xmlContent.replace("${" + placeholder + "}", value);
    }

    // Simple method to replace placeholders
    public static String replacePlaceholders(String content, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            content = content.replace(placeholder, entry.getValue());
        }
        return content;
    }

    public static String extractValueFromXML(String xmlString, String xPathExpression) {
        try {
            // Parse the XML string into a Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

            // Create an XPath instance
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            // Compile and evaluate the XPath expression
            XPathExpression expr = xPath.compile(xPathExpression);
            String value = (String) expr.evaluate(document, XPathConstants.STRING);

            return value;
        } catch (Exception e) {
            return null;
        }

    }

    public static String readXmlWithReplacePlaceholders(String filePath, Map<String, String> placeholders) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                fileContent = fileContent.replace(placeholder, entry.getValue());
            }
            return fileContent;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

