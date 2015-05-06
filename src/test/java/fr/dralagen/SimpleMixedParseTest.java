package fr.dralagen;

/*
 * csv2xml
 *
 * Copyright (C) 2015 dralagen
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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 5/6/15.
 *
 * @author dralagen
 */
public class SimpleMixedParseTest {

  private Csv2xml parser;

  private InputStream csvInput;

  @org.junit.Before
  public void setUp() throws Exception {
    parser = new Csv2xml();

    parser.createNewDocument("test");
    parser.setIndentSize(2);

    csvInput = SimpleMixedParseTest.class.getResourceAsStream("/csv/simpleMixed.csv");
  }

  @org.junit.Test
  public void simpleTest() {
    parser.convert(csvInput, ";", "field");

    ByteArrayOutputStream result = new ByteArrayOutputStream();
    parser.writeTo(result);

    InputStream expected = SimpleMixedParseTest.class.getResourceAsStream("/xml/simple.xml");

    try {
      Assert.assertEquals("Simple csv parsing mixed quote and without quote", IOUtils.toString(expected), IOUtils.toString(result.toByteArray(), "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @org.junit.Test
  public void simpleCompactTest() {
    parser.setCompact(true);
    parser.convert(csvInput, ";", "field");

    ByteArrayOutputStream result = new ByteArrayOutputStream();
    parser.writeTo(result);

    InputStream expected = SimpleMixedParseTest.class.getResourceAsStream("/xml/simpleCompact.xml");

    try {
      Assert.assertEquals("Simple csv parsing mixed quote and without quote on compact mode", IOUtils.toString(expected), IOUtils.toString(result.toByteArray(), "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}