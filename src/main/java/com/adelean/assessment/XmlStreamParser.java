package com.adelean.assessment;

import com.adelean.assessment.model.DocumentRecord;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class XmlStreamParser {
  public static void streamDocuments(Path xmlFile, Consumer<DocumentRecord> consumer) throws Exception {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    try (InputStream in = Files.newInputStream(xmlFile)) {
      XMLEventReader reader = factory.createXMLEventReader(in);
      DocumentRecord current = null;
      String currentElement = null;
      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        if (event.isStartElement()) {
          String name = event.asStartElement().getName().getLocalPart();
          if ("document".equalsIgnoreCase(name)) {
            current = new DocumentRecord();
          } else {
            currentElement = name;
          }
        } else if (event.isCharacters()) {
          if (current != null && currentElement != null) {
            String text = event.asCharacters().getData().trim();
            if (!text.isEmpty()) {
              switch (currentElement.toLowerCase()) {
                case "id":
                  current.setId(text);
                  break;
                case "title":
                  current.setTitle(text);
                  break;
                case "body":
                  current.setBody(text);
                  break;
              }
            }
          }
        } else if (event.isEndElement()) {
          String name = event.asEndElement().getName().getLocalPart();
          if ("document".equalsIgnoreCase(name) && current != null) {
            consumer.accept(current);
            current = null;
          } else {
            currentElement = null;
          }
        }
      }
    }
  }
}
