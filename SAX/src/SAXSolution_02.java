
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;


public class SAXSolution_02 {

    private static final String INPUT_FILE = "data.xml";

    public static void main(String[] args) {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource source = new InputSource(INPUT_FILE);
            parser.setContentHandler(
                    new SAXSolution_02_Handler(outputStreamWriter)
            );
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


class SAXSolution_02_Handler implements ContentHandler {


    Locator locator;

    Integer indent;

    OutputStreamWriter outputStreamWriter;

    private final Integer TAB_SIZE = 4;

    private String currentElementName;

    private final List<String> validationMessages = new LinkedList<>();

    private boolean featuresDetected;

    private int countOfFeatures;

    private StringBuffer numberOfFlats;


    public SAXSolution_02_Handler(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
        indent = 0;
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }


    @Override
    public void startDocument() throws SAXException {
        printIndented("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    @Override
    public void endDocument() throws SAXException {
        //
    }


    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes atts
    ) throws SAXException {

        printIndented(String.format("<%s>", qName));

        currentElementName = qName;

        printAttributes(atts);
        validateIdentifiers(atts);

        if (qName.equals("property")) {
            featuresDetected = false;
            countOfFeatures = 0;
        }
        if (qName.equals("features")) {
            featuresDetected = true;
        }
        if (qName.equals("feature")) {
            ++countOfFeatures;
        }

        if (qName.equals("numberOfFlats")) {
            numberOfFlats = new StringBuffer();
        }

        ++indent;

    }

    @Override
    public void endElement(
            String uri, String localName, String qName
    ) throws SAXException {

        if (localName.equals("real-estate")) {
            printErrors();
        }

        if (localName.equals("property")) {
            if (featuresDetected && (countOfFeatures < 2 || countOfFeatures > 5)) {
                reportError("Property must have at least 2 and at most 5 features");
            }
        }

        if (qName.equals("numberOfFlats")) {
            String s = numberOfFlats.toString().trim();
            try {
                int n = Integer.parseInt(s);
                if (n < 0) {
                    reportError("Number of flats must be a nonnegative number");
                }
            } catch (NumberFormatException e) {
                reportError(
                        String.format("Wrong number format of number of flats: %s", s)
                );
            }
        }

        --indent;
        printIndented(String.format("</%s>", qName));

    }


    @Override
    public void characters(
            char[] chars, int start, int length
    ) throws SAXException {

        String s = new String(chars, start, length).toUpperCase().trim();

        if (s.length() > 0) {

            printIndented(s);

            if (currentElementName.equals("numberOfFlats")) {
                numberOfFlats.append(s);
            }

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
        // https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
        try {
            if (indent > 0) {
                outputStreamWriter.write(
                        String.format("%1$" + (indent * TAB_SIZE) + "s", "")
                );
            }
            outputStreamWriter.write(what + "\r\n");
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printAttributes(Attributes atts) {
        if (atts.getLength() > 0) {
            ++indent;
            for (int i = 0; i < atts.getLength(); ++i) {
                String name = atts.getQName(i);
                printIndented(
                        String.format("<%s>%s</%s>", name, atts.getValue(i), name)
                );
            }
            --indent;
        }
    }


    private void validateIdentifiers(Attributes atts) {

        if (atts.getLength() > 0) {
            if (currentElementName.equals("owner") && (atts.getValue("idOwner") == null || !atts.getValue("idOwner").startsWith("o"))) {
                reportError("Element of type OWNER is expected to have an id that starts with 'o'");
            }
            if (currentElementName.equals("flat") && (atts.getValue("idFlat") == null || !atts.getValue("idFlat").startsWith("f"))) {
                reportError("Element of type FLAT is expected to have an id that starts with 'f'");
            }
            if (currentElementName.equals("property") && (atts.getValue("idProperty") == null || !atts.getValue("idProperty").startsWith("p"))) {
                reportError("Element of type PROPERTY is expected to have an id that starts with 'p'");
            }
            if (currentElementName.equals("agency") && (atts.getValue("idAgency") == null || !atts.getValue("idAgency").startsWith("a"))) {
                reportError("Element of type AGENCY is expected to have an id that starts with 'a'");
            }
        }

    }


    private void reportError(String cause) {
        validationMessages.add(
                String.format(
                        "Error: %s on line %d column %d.",
                        cause, locator.getLineNumber(), locator.getColumnNumber()
                )
        );
    }


    private void printErrors() {
        if (!validationMessages.isEmpty()) {
            printIndented("<errors>");
            ++indent;
            for (String m : validationMessages) {
                printIndented(m);
            }
            --indent;
            printIndented("</errors>");
        }
    }


}
