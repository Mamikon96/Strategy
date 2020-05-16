import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class DOMAnalyseStrategy implements AnalyseStrategy {

    @Override
    public boolean validateAndFix(String sourceFile, String targetFile) {
        try {
            // get Root Node
            Document document = readFromInput(sourceFile);

            // get Subjects Nodes
            NodeList subjects = document.getElementsByTagName("subject");
            double[] marks = retriveMarks(subjects);

            // get Average Node
            Node averageNode = document.getElementsByTagName("average").item(0);
            double average = Double.parseDouble(averageNode.getTextContent());

            if (average == getAverage(marks)) return true;

            // Fix Average
            averageNode.setTextContent(String.valueOf(getAverage(marks)));

            try {
                writeToOutput(document, targetFile);
                return true;
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Document readFromInput(String inputFile) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(inputFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }

    private void writeToOutput(Document document, String outputFile) throws TransformerException {
        File file = new File(outputFile);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
    }

    private double[] retriveMarks(NodeList nodeList) {
        if (nodeList.getLength() == 0) return null;
        double[] marks = new double[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String markText = node.getAttributes().getNamedItem("mark").getNodeValue();
            marks[i] = Double.parseDouble(markText);
        }
        return marks;
    }

    private double getAverage(double... marks) {
        double res = 0;
        for (int i = 0; i < marks.length; i++) {
            res += marks[i];
        }
        return res / marks.length;
    }
}
