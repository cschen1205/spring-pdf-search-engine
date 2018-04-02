package com.github.chen0040.pdf_search.models;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xschen on 16/7/2017.
 */
@Getter
@Setter
public class IndexMap {
   private String name;
   private List<IndexProperty> properties = new ArrayList<>();

   public String toEsJson(){
      Map<String, Object> data = new HashMap<>();
      Map<String, Object> content = new HashMap<>();

      Map<String, Object> properties = new HashMap<>();

      for(IndexProperty p : this.properties) {
         Map<String, Object> pDetail = new HashMap<>();
         pDetail.put("type", p.getType());
         properties.put(p.getName(), pDetail);
      }

      content.put("properties", properties);
      data.put(name, content);

      Map<String, Object> mappings = new HashMap<>();
      mappings.put("mappings", name);

      return JSON.toJSONString(data, SerializerFeature.BrowserCompatible);
   }


   public void addProperty(String name, String type) {
      IndexProperty p = new IndexProperty();
      p.setName(name);
      p.setType(type);
      properties.add(p);
   }
}
