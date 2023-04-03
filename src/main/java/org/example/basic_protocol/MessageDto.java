package org.example.basic_protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDto {
    private String from;
    private String to;
    private String userName;
    private int option;
    private String message;


    public MessageDto(String userName, int option, String message) {
        this.userName = userName;
        this.option = option;
        this.message = message;
        clientToServer();
    }

    public void convertMessage(Function<String,String> function) {
        this.message = function.apply(this.message);
    }

    public MessageDto serverToClient(){
        this.from = "server";
        this.to = "client";
        return this;
    }
    public MessageDto clientToServer(){
        this.from = "client";
        this.to = "server";
        return this;
    }
}
