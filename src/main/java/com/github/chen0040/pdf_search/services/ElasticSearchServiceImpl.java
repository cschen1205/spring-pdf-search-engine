package com.github.chen0040.pdf_search.services;


import com.github.chen0040.pdf_search.models.IndexMap;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 16/7/2017.
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
   private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
   private JestClient client;

   @Value("${es.address}")
   private String ipAddress;

   public ElasticSearchServiceImpl(){
      JestClientFactory factory = new JestClientFactory();
      factory.setHttpClientConfig(new HttpClientConfig
              .Builder(getIpAddress())
              .multiThreaded(true)
              //Per default this implementation will create no more than 2 concurrent connections per given route
              .defaultMaxTotalConnectionPerRoute(4)
              // and no more 20 connections in total
              .maxTotalConnection(60)
                        .build());
      client = factory.getObject();
   }

   @Override
   public JestClient getClient() {
      return client;
   }


   @Override
   public String getIpAddress() {
      return ipAddress;
   }

   @Override
   public void index(Object source, String indexName, String typeName, String id) throws IOException {
      Index index = new Index.Builder(source).index(indexName).type(typeName).id(id).build();
      client.execute(index);
   }

   @Override
   public void index(String indexName) throws IOException {
      client.execute(new CreateIndex.Builder(indexName).build());
   }



   @Override public void indexMapping(String indexName, String typeName, IndexMap im) throws IOException {
      PutMapping putMapping = new PutMapping.Builder(
              indexName,
              typeName,
              im.toEsJson()
      ).build();
      JestResult result = client.execute(putMapping);
      if(result.isSucceeded()) {
         logger.info("Successfully create or update index mapping for {}/{}", indexName, typeName);
      } else {
         logger.error("Failed to create or update index mapping for " + indexName + "/" + typeName);
         logger.error(result.getErrorMessage());
      }
   }



   @Override public void create(Object source, String indexName, String typeName, String id) throws IOException {
      index(source, indexName, typeName, id);
   }


   @Override public void update(Object source, String indexName, String typeName, String id) throws IOException {
      client.execute(new Update.Builder(source).index(indexName).type(typeName).id(id).build());
   }


   @Override
   public <T> T get(String id, String indexName, String typeName, Class<T> clazz) throws IOException {
      Get get = new Get.Builder(indexName, id).type(typeName).build();

      JestResult result = client.execute(get);

      if(result.isSucceeded()) {
         return result.getSourceAsObject(clazz);
      } else {
         return null;
      }
   }


   @Override public boolean exists(String id, String indexName, String typeName) throws IOException {
      Get get = new Get.Builder(indexName, id).type(typeName).build();

      JestResult result = client.execute(get);
      return result.isSucceeded();
   }


   @Override
   public void delete(String indexName, String typeName, String id) throws IOException {
      client.execute(new Delete.Builder(id)
              .index(indexName)
              .type(typeName)
              .build());
   }

   @Override
   public void deleteIndex(String indexName) throws IOException {
      client.execute(new DeleteIndex.Builder(indexName).build());
   }

   @Override
   public <T> List<T> search(String indexName, String typeName, SearchSourceBuilder builder, Class<T> clazz) throws IOException {
      String query = builder.toString();
      //logger.info("Elasticsearch Query: {}", query);

      Search search = new Search.Builder(query)
              // multiple index or types can be added.
              .addIndex(indexName)
              .addType(typeName)
              .build();

      SearchResult result = client.execute(search);

      List<T> res = new ArrayList<>();

      try {
          List<SearchResult.Hit<T, Void>> hits = result.getHits(clazz);
          for (SearchResult.Hit<T, Void> hit : hits) {
              res.add(hit.source);
          }
      }catch(Exception ex) {
          logger.error("Failed to get hits from the search engine", ex);
      }
      return res;
   }

   @Override
   public <T> long count(String indexName, String typeName, SearchSourceBuilder builder, Class<T> clazz) throws IOException {
      String query = builder.toString();
      //logger.info("Elasticsearch Query: {}", query);
      Search search = new Search.Builder(query)
              // multiple index or types can be added.
              .addIndex(indexName)
              .addType(typeName)
              .build();

      SearchResult result = client.execute(search);

      long count = 0L;
      if(result != null) {
         try {
            count = result.getTotal();
         } catch (Exception ex) {
            logger.error("Failed to execute count search", ex);
         }
      }

      return count;


   }
}
