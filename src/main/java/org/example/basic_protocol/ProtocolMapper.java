package org.example.basic_protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.StringTokenizer;

import static org.example.basic_protocol.KeyWord.Method.ERROR;
import static org.example.basic_protocol.KeyWord.Method.MESSAGE;


public class ProtocolMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();



    public String serverToClientMessage(MessageDto messageDto) throws JsonProcessingException {
        return String.format("%s|%s",MESSAGE.name(),objectMapper.writeValueAsString(messageDto.serverToClient()));
    }
    public String serverToClientError(ErrorMessageDto errorMessageDto) throws JsonProcessingException {
        return String.format("%s|%s", ERROR.name(), objectMapper.writeValueAsString(errorMessageDto));
    }

    public String clientToServerMessage(MessageDto messageDto) throws JsonProcessingException {
        return String.format("%s|%s",MESSAGE.name(),objectMapper.writeValueAsString(messageDto.clientToServer()));
    }

    public Object readMessage(String protocolMessage) throws JsonProcessingException {
        StringTokenizer st = new StringTokenizer(protocolMessage,"|");
        String method = st.nextToken();
        Class<?> type = null;
        if (method.equals(MESSAGE.name())){
            type = MessageDto.class;
        } else if (method.equals(ERROR.name())) {
            type = ErrorMessageDto.class;
        }
        return objectMapper.readValue(st.nextToken(),type);
    }
}
