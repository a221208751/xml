package XML_SAX;

import org.w3c.dom.ls.LSOutput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Sales_Dept_State extends DefaultHandler {
    private static final String CLASS_NAME = Sales_Dept_State.class.getName();
    private final static Logger LOG = Logger.getLogger(CLASS_NAME);

    private SAXParser parser = null;
    private SAXParserFactory spf;

    private double totalSalesState;
    private double totalSalesDept;
    private boolean inSales1;
    private boolean inSales2;

    private String currentElement;
    private String id;
    private String name;
    private String lastName;
    private String sales;
    private String state;
    private String dept;

    private String keyword;

    private HashMap<String, Double> subtotalesState;
    private HashMap<String, Double> subtotalesDept;

    public static void main(String args[]) {
        if (args.length == 0) {
            LOG.severe("No file to process. Usage is:" + "\njava DeptSalesReport <keyword>");
            return;
        }
        File xmlFile = new File(args[0]);
        Sales_Dept_State handler = new Sales_Dept_State();
        handler.process( xmlFile );
    }

    public Sales_Dept_State(){
        super();
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);

        subtotalesState = new HashMap<>();
        subtotalesDept = new HashMap<>();
    }
    private void process(File file){
        try{
            parser = spf.newSAXParser();
        } catch (SAXException | ParserConfigurationException e){
            LOG.severe(e.getMessage());
            System.exit(1);
        }
        System.out.println("\nStarting parsing of " + file + "\n");
        try{
            keyword = state;
            parser.parse(file, this);
        } catch (IOException | SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        totalSalesState = 0.0;
        totalSalesDept = 0.0;
    }

    @Override
    public void endDocument() throws SAXException {
        Set<Map.Entry<String,Double>> entries1 = subtotalesState.entrySet();
        System.out.println("Ventas por estado:");
        for (Map.Entry<String,Double> entry: entries1) {
            System.out.printf("%-15.15s $%,9.2f\n",entry.getKey(),entry.getValue());
        }

        Set<Map.Entry<String,Double>> entries2 = subtotalesDept.entrySet();
        System.out.println("Ventas por departamento:");
        for (Map.Entry<String,Double> entry: entries2) {
            System.out.printf("%-15.15s $%,9.2f\n",entry.getKey(),entry.getValue());
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("sale_record")) {
            inSales1 = true;
            inSales2 = true;
        }
        currentElement = localName;
    }

    @Override
    public void characters(char[] bytes, int start, int length) throws SAXException {

        switch (currentElement) {
            case "id":
                this.id = new String(bytes, start, length);
                break;
            case "first_name":
                this.name = new String(bytes, start, length);
                break;
            case "last_name":
                this.lastName = new String(bytes, start, length);
                break;
            case "sales":
                this.sales = new String(bytes, start, length);
                break;
            case "state":
                this.state = new String(bytes, start, length);
                break;
            case "department":
                this.dept = new String(bytes, start, length);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("sale_record")) {
            double val1 = 0.0;
            try {
                val1 = Double.parseDouble(this.sales);
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }

            if (subtotalesState.containsKey(this.state)) {
                double sum1 = subtotalesState.get(this.state);
                subtotalesState.put(this.state, sum1 + val1);
            } else {
                subtotalesState.put(this.state, val1);
            }
            totalSalesState = totalSalesState + val1;
            inSales1 = false;
        }

        if (localName.equals("sale_record")) {
            double val2 = 0.0;
            try {
                val2 = Double.parseDouble(this.sales);
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }

            if (subtotalesDept.containsKey(this.dept)) {
                double sum2 = subtotalesDept.get(this.dept);
                subtotalesDept.put(this.dept, sum2 + val2);
            } else {
                subtotalesDept.put(this.dept, val2);
            }
            totalSalesDept = totalSalesDept + val2;
            inSales2 = false;
        }
    }

        private void printRecord() {
            System.out.printf(id, name, lastName, sales, state, dept);
        }
    }

