import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLHandler {

    private String inputFile;
    private String outputFile;
    private AnalyseStrategy strategy;

    public XMLHandler(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void setStrategy(AnalyseStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean handle() {
        return strategy.validateAndFix(inputFile, outputFile);
    }
}
