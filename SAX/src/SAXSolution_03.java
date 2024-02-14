
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class SAXSolution_03 {

    private static final String INPUT_FILE = "data.xml";

    public static void main(String[] args) {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource source = new InputSource(INPUT_FILE);
            parser.setContentHandler(
                    new SAXSolution_03_Handler(outputStreamWriter)
            );
            parser.parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}


class SAXSolution_03_Handler implements ContentHandler {


    private class Flat {
        String name;
        String comfortLevel;
        int rate;
        List<String> features = new ArrayList<>();
    }


    Locator locator;

    Integer indent;

    OutputStreamWriter outputStreamWriter;

    private final Integer TAB_SIZE = 4;

    private final List<Flat> flats = new ArrayList<>();

    private String currentElement;

    private Flat currentFlat;

    private boolean inFlat;

    private StringBuffer stringBuffer;


    public SAXSolution_03_Handler(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
        indent = 0;
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }


    @Override
    public void startDocument() throws SAXException {

        printIndented("<!DOCTYPE html>");
        printIndented("<html>");
        ++indent;
        printIndented("<head><title>Papers Overview</title></head>");
        printIndented("<body>");
        ++indent;
        printIndented("<table style=\"border: 1px solid black;\">");
        ++indent;
        printIndented("<thead><tr><th>Flat Name</th><th>Monthly rate</th><th>Comfort level</th><th>Features</th></tr></thead>");
        printIndented("<tbody>");
        ++indent;

    }

    @Override
    public void endDocument() throws SAXException {

        int countOfFeatures = 0;

        for (Flat f : flats) {

            StringBuffer features = new StringBuffer();
            for (int i = 0; i < f.features.size()-2; i++) {
                if (i > 0) {
                    features.append(", ");
                }
                features.append(f.features.get(i));
            }

            printIndented(
                    String.format(
                            "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                            f.name, f.rate, f.comfortLevel, features)
            );

            countOfFeatures += f.features.size();

        }

        --indent;
        printIndented("</tbody>");
        printIndented(
                String.format("<tfoot><tr><td>Total count of features:</td><td>%d</td><td></td><td></td></tr></tfoot>", countOfFeatures-6)
        );
        --indent;
        printIndented("</table>");
        --indent;
        printIndented("</body>");
        --indent;
        printIndented("</html>");

    }


    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes atts
    ) throws SAXException {

        currentElement = localName;

        if (currentElement.equals("flat")) {
            currentFlat = new Flat();
            currentFlat.comfortLevel = atts.getValue("comfort");
            flats.add(currentFlat);
            inFlat = true;
        }

        if (inFlat && (
                currentElement.equals("name") ||
                        currentElement.equals("feature") ||
                        currentElement.equals("rate")
        )
        ) {
            stringBuffer = new StringBuffer();
        }

    }

    @Override
    public void endElement(
            String uri, String localName, String qName
    ) throws SAXException {

        if (inFlat) {
            if (currentElement.equals("name")) {
                currentFlat.name = stringBuffer.toString();
            }
            if (currentElement.equals("feature")) {
                currentFlat.features.add(stringBuffer.toString());
            }
            if (currentElement.equals("rate")) {
                currentFlat.rate = Integer.parseInt(stringBuffer.toString());
            }
        }

        if (localName.equals("flat")) {
            inFlat = false;
        }

    }


    @Override
    public void characters(
            char[] chars, int start, int length
    ) throws SAXException {

        String s = new String(chars, start, length).trim();

        if (inFlat && (
                currentElement.equals("name") ||
                        currentElement.equals("feature") ||
                        currentElement.equals("rate")
        )
        ) {
            stringBuffer.append(s);
        }

    }


    @Override
    public void startPrefixMapping(
            String prefix, String uri
    ) throws SAXException {
        // ...
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // ...
    }

    @Override
    public void ignorableWhitespace(
            char[] chars, int start, int length
    ) throws SAXException {
        // ...
    }

    @Override
    public void processingInstruction(
            String target, String data
    ) throws SAXException {
        // ...
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // ...
    }


    private void printIndented(String what) {
        try {
            if (indent > 0) {
                outputStreamWriter.write(String.format("%1$" + (indent * TAB_SIZE) + "s", ""));
            }
            outputStreamWriter.write(what + "\r\n");
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
