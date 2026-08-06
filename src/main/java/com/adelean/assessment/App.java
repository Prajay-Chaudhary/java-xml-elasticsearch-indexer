package com.adelean.assessment;

import com.adelean.assessment.model.DocumentRecord;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

public class App {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.err.println("Usage: java -jar app.jar <xml.zip> <tmp-unzip-dir> <es-index-name> <es-host:port>");
      System.exit(1);
    }
    Path zipPath = Paths.get(args[0]);
    Path tmpDir = Paths.get(args[1]);
    String indexName = args[2];
    String[] hostPort = args[3].split(":");
    String host = hostPort[0];
    int port = Integer.parseInt(hostPort[1]);

    Files.createDirectories(tmpDir);
    XmlUnzipper.unzipToDirectory(zipPath, tmpDir);

    try (EsIndexer indexer = new EsIndexer(host, port, indexName)) {
      try (Stream<Path> files = Files.walk(tmpDir)) {
        files.filter(p -> p.toString().endsWith(".xml")).forEach(xmlFile -> {
          try (InputStream in = Files.newInputStream(xmlFile)) {
            XmlProductParser.parseProducts(in, (Map<String, Object> docMap) -> {
              try {
                System.out.println("Parsed product sku=" + docMap.get("article_sku") + " brand=" + docMap.get("brand"));
                DocumentRecord doc = MAPPER.convertValue(docMap, DocumentRecord.class);
                if ((doc.getId() == null || doc.getId().isEmpty()) && doc.getArticleSku() != null) {
                  doc.setId(doc.getArticleSku());
                }
                indexer.index(doc);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
          } catch (XMLStreamException xe) {
            throw new RuntimeException("XML parsing failed for file: " + xmlFile, xe);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
      }
    }
    System.out.println("Indexing finished.");
  }
}
