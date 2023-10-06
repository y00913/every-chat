package com.example.everychat.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessage {
    private StatusEnum status;
    private String message;
    private Object data;

    public ResponseMessage(){
        this.status = StatusEnum.BAD_REQUEST;
        this.message = null;
        this.data = null;
    }

    public ResponseMessage(StatusEnum status, String message){
        this.status = status;
        this.message = message;
    }
}
