package com.esdllm.exception;

import lombok.Getter;

@Getter
public class BilibiliException extends RuntimeException {
   private final int  code;
   private final String message;
   private final String description;
   public BilibiliException(String message) {
     super(message);
     this.code=0;
     this.message = message;
     this.description = "";
   }
   private BilibiliException(int code, String message, String description) {
     super(message);
     this.code = code;
     this.message = message;
     this.description = description;
   }
}
