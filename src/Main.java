public class Main {
    public static void main(String[] args) {
        if (args.length < 2) return;

        String inputFileName = args[0];
        String outputFileName = args[1];

        AnalyseStrategy strategyDOM = new DOMAnalyseStrategy();
        AnalyseStrategy strategySAX = new SAXAnalyseStrategy();

        XMLHandler xmlHandler = new XMLHandler(inputFileName, outputFileName);
        xmlHandler.setStrategy(strategyDOM);
//        xmlHandler.setStrategy(strategySAX);
        if (xmlHandler.handle()) {
            System.out.println("Validated and Fixed: Successfully");
        } else {
            System.out.println("Validated and Fixed: Failed");
        }
    }
}
