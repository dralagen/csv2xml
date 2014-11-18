package org.dralagen;

/*
 * csv2xml
 *
 * Copyright (C) 2014 dralagen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created on 14/11/14.
 *
 * @author dralagen
 */
public class Csv2xml {

    private DocumentBuilderFactory domFactory = null;
    private DocumentBuilder        domBuilder = null;

    private Document document;

    private Node currentElement;

    public Csv2xml() {
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

    /**
     * Create a new document
     *
     * If the document already exist, it will be erased
     *
     * @param node name of root node
     */
    public void createNewDocument(String node) {

        document = domBuilder.newDocument();
        // Root element
        Element element = document.createElement(node);
        document.appendChild(element);

        currentElement = (Node) element;

    }

    /**
     * Create a new document with default name "document"
     *
     * If the document already exist, it will be erased
     */
    public void createNewDocument() {
        createNewDocument("document");
    }

    /**
     * Add a new child node into the document
     *
     * Create default document if not created
     *
     * @param node name of new node
     */
    public void addNode(String node) {
        if ( document == null ) {
            createNewDocument();
        }
        Element element = document.createElement(node);
        currentElement.appendChild(element);

        currentElement = (Node) element;
    }

    /**
     * Move your cursor of current element to parent node
     */
    public void parent() {
        currentElement = currentElement.getParentNode();
    }

    /**
     * Convert the csv input stream into a internal document xml
     *
     * @param csv InputStream contain your csv file
     * @param delimiter the delimiter character of csv field
     * @param nodeRow name of node who receive all values of one row
     * @return number of rows converted
     */
    public int convert(InputStream csv, String delimiter, String nodeRow) {

        int rowsCount = 0;
        try {
            // Read csv file
            BufferedReader csvReader;
            csvReader = new BufferedReader(new InputStreamReader(csv));

            List<String> headers = new ArrayList<String>();

            { // Header row
                String text = null;

                // Header row
                if ( (text = csvReader.readLine()) != null ) {
                    String[] rowValues = text.split(delimiter);
                    Collections.addAll(headers, rowValues);
                }

            }


            {  // Data rows
                List<String> rowValues = null;
                while ( (rowValues = split(csvReader, delimiter, headers.size())) != null ) {

                    Element rowElement = document.createElement(nodeRow);
                    currentElement.appendChild(rowElement);

                    for ( int col = 0; col < headers.size(); col++ ) {

                        String header = headers.get(col);
                        String value = "";

                        if ( col < rowValues.size() ) {
                            value = rowValues.get(col);
                        }

                        Element curElement = document.createElement(header);
                        curElement.appendChild(document.createTextNode(value));
                        rowElement.appendChild(curElement);

                    }

                    rowsCount++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    /**
     * Convert the csv input stream into a internal document xml and use default name of node row
     *
     * @param csv InputStream contain your csv file
     * @param delimiter the delimiter character of csv field
     * @return number of rows converted
     */
    public int convert(InputStream csv, String delimiter) {
        return convert(csv, delimiter, "element");
    }

    /**
     * Write the xml document in out
     *
     * You can use <code>writeTo(System.out)</code> to write the xml result into your console
     *
     * @param out Write the xml document in output
     */
    public void writeTo(OutputStream out) {
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

            Source src = new DOMSource(document);
            Result result = new StreamResult(osw);
            aTransformer.transform(src, result);

            osw.flush();
            String output = new String(baos.toByteArray());
            out.write(output.getBytes());

        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            try {
                osw.close();
            } catch (Exception ignored) {
            }
            try {
                baos.close();
            } catch (Exception ignored) {
            }
        }
    }

    /*


     */

    private List<String> split(BufferedReader reader, String delimiter, int limit) throws IOException {

        String text = reader.readLine();

        if (text == null) {
            return null;
        }

        // text.split(delimiter) delete end empty field
        String[] splited =  text.split(delimiter, Integer.MAX_VALUE);

        List<String> result = new ArrayList<String>();

        int i = 0;
        while (i < splited.length) {
            int j = i;

            if (!splited[i].equals("") && splited[i].charAt(0) == '"' && splited[i].charAt(splited[i].length()-1) != '"') {
                // delete the " unnessaisery
                splited[i] = splited[i].substring(1);

                ++j;
                if (j < splited.length) {
                    while ( j < splited.length && splited[j].charAt(splited[j].length() - 1) != '"' ) {
                        splited[i] += ";" + splited[j];
                        ++j;
                    }
                    splited[i] += ";" + splited[j];
                }

                if (j < splited.length) {
                    splited[i] = splited[i].substring(0, splited[i].length() - 2);
                }
            }

            if (!splited[i].equals("") && splited[i].charAt(0) == '"' && splited[i].charAt(splited[i].length()-1) == '"') {
                result.add(splited[i].substring(1, splited[i].length() - 2));
            }
            else {
                result.add(splited[i]);
            }
            i = j+1;
        }


        // complete line who field contain '\n'
        if ( result.size() < limit ) {
            List<String> extendsRowValues = null;
            if ((extendsRowValues = split(reader, delimiter, limit - result.size())) != null) {

                int rowValuesLastIndex = result.size() - 1;

                result.set(rowValuesLastIndex, result.get(rowValuesLastIndex) + "\n" + extendsRowValues.get(0));

                if ( extendsRowValues.size() > 1 ) {
                    result.addAll(extendsRowValues.subList(1, extendsRowValues.size()));
                }
            }
        }

        return result;
    }


    /**
     * Create an InputStream form an url or a path of fileSystem
     *
     * @see java.net.URL
     * @see java.io.FileInputStream
     *
     * @param inputName is an URL or a path of file
     * @return InputStream form inputName
     * @throws java.io.IOException if a error in read the input
     */
    public static InputStream getInputStream(String inputName) throws IOException {
        InputStream inputStream = null;

        try {
            URL url = new URL(inputName);
            inputStream = url.openStream();
        } catch (MalformedURLException e) {

            inputStream = new FileInputStream(
                    new File(inputName)
            );

        }

        return inputStream;
    }

    public static void main (String[] args) {
        if (args.length != 3) {
            System.out.println("Usage : csv2xml \"path/of/input/file.csv\" \"path/of/output/file.xml\" \";\"");
            System.exit(1);
        }

        Csv2xml converter = new Csv2xml();

        converter.createNewDocument();
        converter.addNode("data");

        InputStream csvInput = null;
        try {
            csvInput = Csv2xml.getInputStream(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        converter.convert(csvInput, args[2], "element");

        //converter.writeTo(System.out);

        OutputStream xmlOutput;
        try {
            xmlOutput = new FileOutputStream(args[1]);

            converter.writeTo(xmlOutput);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
