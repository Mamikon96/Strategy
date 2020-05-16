import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderAdapter;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SAXAnalyseStrategy implements AnalyseStrategy {

    @Override
    public boolean validateAndFix(String sourceFile, String targetFile) {
        try {
//            File file = new File(sourceFile);
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser parser = factory.newSAXParser();
//            SAXHandler handler = new SAXHandler();
//            parser.parse(file, handler);
//
            return validateFile(sourceFile, targetFile);

//            InputSource inputSource =
//                    new InputSource(
//                            new BufferedReader(new FileReader(sourceFile)));
//            XMLReader saxReader = new CustomXMLReader();
//            SAXSource source = new SAXSource(saxReader,inputSource);
//            StreamResult result = new StreamResult(new File(targetFile));
//
//            TransformerFactory tFactory =
//                    TransformerFactory.newInstance();
//            Transformer transformer = tFactory.newTransformer();
//            transformer.transform(source,result);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean validateFile(String inputFile, String outputFile) throws ParserConfigurationException, SAXException, IOException {
        File file = new File(inputFile);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
//        SAXHandler handler = new SAXHandler();
        PrintWriter out = new PrintWriter(new FileWriter(new File(outputFile)));
        SAXHandler handler = new SAXHandler(out);
        parser.parse(file, handler);
        out.flush();
        out.close();
        return true;
    }

    private void writeToOutput(Document document, String outputFile) throws TransformerException {
        File file = new File(outputFile);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
    }



    private class SAXHandler extends DefaultHandler {

        private static final String SUBJECT = "subject";
        private static final String AVERAGE = "average";
        private static final String MARK = "mark";

        private List<Double> marks;
        private double average;
        private boolean isAverageElement;

        private String elementValue;

        private PrintWriter out;


        public SAXHandler(PrintWriter printWriter) {
            marks = new ArrayList<>();
            out = printWriter;
        }


        @Override
        public void startDocument() throws SAXException {
            out.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        }

        @Override
        public void endDocument() throws SAXException {
            double calcAvg = getAverage(marks);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            isAverageElement = false;
            switch (qName) {
                case SUBJECT:
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (MARK.equalsIgnoreCase(attributes.getLocalName(i))) {
                            marks.add(Double.parseDouble(attributes.getValue(i)));
                        }
                    }
                    break;
                case AVERAGE:
                    isAverageElement = true;
                    break;
                default:
                    break;
            }

            if (attributes.getLength() > 0) {

                String tag = "<" + qName;
                for (int i = 0; i < attributes.getLength(); i++) {

                    tag += " " + attributes.getLocalName(i) + "=\""
                            + attributes.getValue(i) + "\"";
                }

                tag += ">";
                out.print(tag);

            } else {
                out.print("<" + qName + ">");
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            isAverageElement = false;
            switch (qName) {
                case AVERAGE:
                    average = Double.parseDouble(elementValue);
                    break;
            }

            out.print("</" + qName + ">");
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            elementValue = new String(ch, start, length);
            if (isAverageElement && marks.size() != 0) {
                out.print(getAverage(marks));
            } else {
                out.print(elementValue);
            }
        }

        private double getAverage(List<Double> marks) {
            double res = 0;
            for (int i = 0; i < marks.size(); i++) {
                res += marks.get(i);
            }
            return res / marks.size();
        }
    }
}
