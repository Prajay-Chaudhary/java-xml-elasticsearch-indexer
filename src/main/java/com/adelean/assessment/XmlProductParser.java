package com.adelean.assessment;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class XmlProductParser {
  public static void parseProducts(InputStream xmlIn, Consumer<Map<String, Object>> onProduct)
      throws XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader r = factory.createXMLStreamReader(xmlIn, "UTF-8");

    Map<String, String> current = null;
    String currentElement = null;

    while (r.hasNext()) {
      int ev = r.next();
      if (ev == XMLStreamConstants.START_ELEMENT) {
        String name = r.getLocalName();
        if ("product".equals(name)) {
          current = new HashMap<>();
        } else if (current != null) {
          currentElement = name;
        }
      } else if ((ev == XMLStreamConstants.CHARACTERS || ev == XMLStreamConstants.CDATA)
          && current != null && currentElement != null) {
        String text = r.getText();
        if (!text.trim().isEmpty()) {
          current.merge(currentElement, text, (a, b) -> a + b);
        }
      } else if (ev == XMLStreamConstants.END_ELEMENT) {
        String name = r.getLocalName();
        if ("product".equals(name) && current != null) {
          Map<String, Object> doc = new HashMap<>(current);
          onProduct.accept(doc);
          current = null;
        } else {
          currentElement = null;
        }
      }
    }
    r.close();
  }
}
