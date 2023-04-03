package org.example.basic_protocol;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorMessageDto {

    private String from = "server";
    private String to = "client";
    private int errorcode;
    private String message;

    public ErrorMessageDto(int errorcode, String message) {
        this.errorcode = errorcode;
        this.message = message;
    }
}
