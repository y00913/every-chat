package com.example.everychat.enums;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.Charset;

public class MakeResponse {
    public static ResponseEntity getResponseMessage(StatusEnum status, String message, Object data) {
        ResponseMessage responseMessage = new ResponseMessage(status, message, data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(responseMessage, headers,exceptionMessage(status));
    }

    public static ResponseEntity getResponseMessage(StatusEnum status, String message) {
        ResponseMessage responseMessage = new ResponseMessage(status, message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(responseMessage, headers,exceptionMessage(status));
    }

    public static HttpStatus exceptionMessage(StatusEnum status){
        HttpStatus returnStatus = null;
        if(status.equals(StatusEnum.OK)){
            returnStatus = HttpStatus.OK;
        }else if(status.equals(StatusEnum.BAD_REQUEST)){
            returnStatus = HttpStatus.BAD_REQUEST;
        }else if(status.equals(StatusEnum.NOT_FOUND)){
            returnStatus = HttpStatus.NOT_FOUND;
        }else if(status.equals(StatusEnum.INTERNAL_SERER_ERROR)){
            returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return returnStatus;
    }
}
