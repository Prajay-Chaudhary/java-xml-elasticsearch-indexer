# adelean-java-assessment

A small Java tool that **extracts** `xml.zip`, **stream-parses** XML documents using constant memory, and **bulk-indexes** them into a single Elasticsearch 7 index.

## Prerequisites

- **Elasticsearch 7.4.1** running and accessible (for example, `localhost:9200`)
- **Java 11**
- **Maven**

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

Thank you for reviewing this assessment. I appreciate your time and consideration.
