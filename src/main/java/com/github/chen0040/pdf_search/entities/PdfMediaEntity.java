package com.github.chen0040.pdf_search.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by xschen on 23/6/2017.
 */
@Entity
@Table(name = "pdf_media", indexes = {
        @Index(name = "documentIdIndex", columnList = "documentId")
})
@Getter
@Setter
public class PdfMediaEntity {
   @Id
   @GeneratedValue
   private long id;

   private String documentId;

   @Lob
   private byte[] model;

   @Temporal(TemporalType.TIMESTAMP)
   private Date createdAt;

   @Temporal(TemporalType.TIMESTAMP)
   private Date updatedAt;



}
