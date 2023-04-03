package org.example.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.basic_protocol.ErrorMessageDto;
import org.example.basic_protocol.KeyWord;
import org.example.basic_protocol.MessageDto;
import org.example.basic_protocol.ProtocolMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WebApplication {


    private final ProtocolMapper protocolMapper = new ProtocolMapper();

    public void socketHandler(Socket socket){

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            //소켓 생성 후 프로세싱
            String inputLine = "";
            try {
                while ((inputLine = in.readLine()) != null) {
                    //처리 로직
                    System.out.printf("WebApplication %d >> receive : %s\n",this.hashCode(),inputLine);
                    out.println(processMessage(inputLine));

                }
            } catch (ClientExitException e){ //클라이언트가 종료요청
                out.println(KeyWord.EXIT_CONFIRM);
            }
        } catch (Exception e){
            throw new RuntimeException("예상치 못한 예외");
        }
        System.out.println("SYSTEM_LOG : WebApplication.java 웹 애플리케이션 종료");
    }




    //메시지를 옵션에 따라 처리
    private String processMessage(String line) throws ClientExitException, JsonProcessingException {
        MessageDto messageDto;
        //예외 처리
        try {
            messageDto = (MessageDto)protocolMapper.readMessage(line);
        } catch (DatabindException e){
            if (e.getCause().getMessage().contains("option")){
                return protocolMapper.serverToClientError(new ErrorMessageDto(401,"option input error: non-integer"));
            } else {
                return protocolMapper.serverToClientError(new ErrorMessageDto(402, "Bad Request"));
            }
        }
        //종료 사인-----------------------
        if (messageDto.getMessage().equals(KeyWord.EXIT_SIGN)){
            throw new ClientExitException();
        }

        if (messageDto.getOption() < 1){
            return protocolMapper.serverToClientError(new ErrorMessageDto(403,"option input error: integer less than 1"));
        } else if (messageDto.getOption() > 3) {
            return protocolMapper.serverToClientError(new ErrorMessageDto(404,"option input error: integer greater than 3"));
        }



        //옵션에 따른 처리
        System.out.printf("WebApplication %d >> Before : [%s] : %s\n",this.hashCode(),messageDto.getUserName(),messageDto.getMessage());
        switch (messageDto.getOption()) {
            case 1:
                break; //일반 적인 에코이므로 아무것도 안함
            case 2:
                messageDto.convertMessage(String::toLowerCase);
                break; //소문자로
            case 3:
                messageDto.convertMessage(String::toUpperCase);
                break; //대문자로
        }
        System.out.printf("WebApplication %d >> After : [%s] : %s\n",this.hashCode(),messageDto.getUserName(),messageDto.getMessage());
        return protocolMapper.serverToClientMessage(messageDto);
    }
}
