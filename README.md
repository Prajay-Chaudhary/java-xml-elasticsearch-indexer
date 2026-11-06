# Java XML to Elasticsearch Indexer

A Java application that extracts XML files from a ZIP archive, stream-parses large XML documents with low memory usage, and bulk-indexes product data into Elasticsearch.

The project demonstrates efficient processing of large datasets using streaming XML parsing instead of loading complete documents into memory.

## Features

- Extracts XML files from ZIP archives
- Stream-parses XML documents for memory-efficient processing
- Processes product information such as SKU, brand, and description
- Bulk-indexes parsed documents into Elasticsearch
- Supports configurable input paths, temporary directories, index names, and Elasticsearch hosts
- Packaged as an executable shaded JAR

## Tech Stack

- Java 11
- Maven
- Elasticsearch 7.4.1
- XML streaming
- Elasticsearch Bulk API

## Prerequisites

Make sure the following are installed and available:

- Java 11
- Maven
- Elasticsearch 7.4.1

Elasticsearch should be running and accessible, for example:

`localhost:9200`

## Build

```bash
mvn clean package
```

This generates a shaded JAR in the `target/` directory, for example:

```text
target/adelean-java-assessment-1.0.0-shaded.jar
```

## Run

### 1. (Optional): run this to create the index with the expected mapping before running the app.

```bash
curl -X PUT "http://localhost:9200/my_index" \
  -H "Content-Type: application/json" \
  -d '{
    "mappings": {
      "properties": {
        "article_sku": {
          "type": "keyword"
        },
        "brand": {
          "type": "keyword"
        },
        "full_description": {
          "type": "text"
        }
      }
    }
  }'
```

### 2. Run the application

```bash
# Usage:
# <zip-file> <temporary-unzip-directory> <index-name> <elasticsearch-host:port>

java -jar target/adelean-java-assessment-1.0.0-shaded.jar \
  /path/to/xml.zip \
  /tmp/adelean-unzip \
  my_index \
  localhost:9200 2>&1 | tee /tmp/adelean-run.log
```

## Expected Output

The console should display messages similar to:

```text
Parsed product sku=000c66dawt brand=HUAWEI
Indexing finished.
```

## Verify the Index

List all indices:

```bash
curl "http://localhost:9200/_cat/indices?v"
```

Retrieve a few indexed documents:

```bash
curl -H "Content-Type: application/json" \
  "http://localhost:9200/my_index/_search?pretty" \
  -d '{
    "size": 5,
    "query": {
      "match_all": {}
    }
  }'
```

Check that products were parsed successfully:

```bash
grep -i "Parsed product sku" /tmp/adelean-run.log || true
```

## Thank You
