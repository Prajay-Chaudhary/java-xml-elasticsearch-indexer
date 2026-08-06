package com.adelean.assessment;

import com.adelean.assessment.model.DocumentRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EsIndexer implements Closeable {
  private final RestHighLevelClient client;
  private final BulkProcessor bulkProcessor;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String indexName;

  public EsIndexer(String host, int port, String indexName) {
    this.client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    this.indexName = indexName;
    BulkProcessor.Listener listener = new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        if (response.hasFailures()) {
          System.err.println("Bulk failures: " + response.buildFailureMessage());
        }
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        failure.printStackTrace();
      }
    };
    BulkProcessor.Builder builder = BulkProcessor.builder(
        (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
        listener);
    builder.setBulkActions(1000);
    builder.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB));
    builder.setFlushInterval(TimeValue.timeValueSeconds(10));
    builder.setConcurrentRequests(1);
    this.bulkProcessor = builder.build();
  }

  public void index(DocumentRecord doc) throws Exception {
    String json = mapper.writeValueAsString(doc);
    IndexRequest req = new IndexRequest(indexName).source(json, XContentType.JSON);
    if (doc.getId() != null)
      req.id(doc.getId());
    bulkProcessor.add(req);
  }

  @Override
  public void close() throws IOException {
    try {
      bulkProcessor.awaitClose(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    client.close();
  }
}
