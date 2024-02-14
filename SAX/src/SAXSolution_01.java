
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class SAXSolution_01 {

    private static final String INPUT_FILE = "data.xml";

    public static void main(String[] args) {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource source = new InputSource(INPUT_FILE);
            parser.setContentHandler(
                    new SAXSolution_01_Handler(outputStreamWriter)
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


class SAXSolution_01_Handler implements ContentHandler {

    //instance variables
     Locator locator;

    Integer indent;

    OutputStreamWriter outputStreamWriter;

    private final Integer TAB_SIZE = 4;


    //constructor
    public SAXSolution_01_Handler(OutputStreamWriter outputStreamWriter) {
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
        // ...
    }


    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes atts
    ) throws SAXException {
        printIndented(String.format("<%s>", qName));
        printAttributes(atts);
        ++indent;
    }

    @Override
    public void endElement(
            String uri, String localName, String qName
    ) throws SAXException {
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


}