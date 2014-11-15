package org.dralagen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created on 14/11/14.
 *
 * @author dralagen
 */
public class ParserCsv2xml {

    protected DocumentBuilderFactory domFactory = null;
    protected DocumentBuilder domBuilder = null;

    public ParserCsv2xml() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (FactoryConfigurationError exp) {
            System.err.println(exp.toString());
        } catch (ParserConfigurationException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
    }

    public int convertFile(String csvFileName, String xmlFileName,
                           String delimiter) {

        int rowsCount = -1;
        try {
            Document newDoc = domBuilder.newDocument();
            // Root element
            Element documentElement = newDoc.createElement("document");
            newDoc.appendChild(documentElement);

            Element rootElement = newDoc.createElement("data");
            documentElement.appendChild(rootElement);

            // Read csv file
            BufferedReader csvReader;
            csvReader = new BufferedReader(new FileReader(csvFileName));

            List<String> headers = new ArrayList<String>();
            String text = null;

            // Header row
            if( (text = csvReader.readLine()) != null) {
                String[] rowValues = text.split(delimiter);
                for (String col : rowValues) {
                    headers.add(col);
                }
            }

            // Data rows
            while ((text = csvReader.readLine()) != null) {

                List<String> rowValues = new ArrayList<String>(Arrays.asList(text.split(delimiter, headers.size())));


                // complete ligne who field contain '\n'
                while (rowValues.size() < headers.size()) {
                    if ((text = csvReader.readLine()) != null ) {
                        String[] extendsRowValues = text.split(delimiter);

                        int rowValuesLastIndex = rowValues.size() - 1;

                        rowValues.set(rowValuesLastIndex, rowValues.get(rowValuesLastIndex) + "\n" + extendsRowValues[0]);

                        if (extendsRowValues.length > 1) {
                            for ( int i = 1; i < extendsRowValues.length; i++ ) {
                                rowValues.add(extendsRowValues[i]);
                            }
                        }
                    }
                }

                rowsCount++;

                Element rowElement = newDoc.createElement("element");
                rootElement.appendChild(rowElement);
                for (int col = 0; col < headers.size(); col++) {

                    String header = headers.get(col);
                    String value = null;

                    value = rowValues.get(col);

                    Element curElement = newDoc.createElement(header);
                    curElement.appendChild(newDoc.createTextNode(value));
                    rowElement.appendChild(curElement);

                }

            }

            ByteArrayOutputStream baos = null;
            OutputStreamWriter osw = null;

            try {

                baos = new ByteArrayOutputStream();
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");

                Source src = new DOMSource(newDoc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                String output = new String(baos.toByteArray());
                OutputStream out = new FileOutputStream(xmlFileName);
                out.write(output.getBytes());
                System.out.println("File " + csvFileName + " is successful parsed in " + xmlFileName);
                //System.out.println(output);


            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                try {
                    osw.close();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }

        } catch (IOException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    public static void main (String[] args) {
        if (args.length != 3) {
            System.out.println("Usage : csv2xml \"path/of/input/file.csv\" \"path/of/output/file.xml\" \";\"");
            System.exit(1);
        }

        ParserCsv2xml parser = new org.dralagen.ParserCsv2xml();
        parser.convertFile(args[0], args[1], args[2]);
    }
}

