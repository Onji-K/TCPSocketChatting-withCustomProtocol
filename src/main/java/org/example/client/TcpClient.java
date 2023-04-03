package org.example.client;

import org.example.basic_protocol.ErrorMessageDto;
import org.example.basic_protocol.KeyWord;
import org.example.basic_protocol.MessageDto;
import org.example.basic_protocol.ProtocolMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
    private final String hostName;
    private final int portNumber;

    private final ProtocolMapper protocolMapper = new ProtocolMapper();

    private static final String BOUNDARY = "===========================================================";


    public TcpClient(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }



    public void startChatting(int retryTime){
        Socket clientSocket = null;
        try {
            clientSocket = attemptConnect(retryTime);
        } catch (ConnectionFailedException e) {
            System.out.println("서버 접속에 실패했습니다.");
        }

        //소켓 정상적 생성
        //메시지 입력
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            System.out.println(BOUNDARY);
            System.out.println("안녕하세요! 채팅 서버에 정상적으로 접속하셨습니다.");
            System.out.println(BOUNDARY);

            String name = null;
            int option = -1;
            String message = "";

            while (name == null){
                System.out.println("당신의 이름을 입력해주세요.");
                name = userInput.readLine().trim(); //이름 입력
                if (name.isEmpty()) name = null; //스페이스 바만 입력했으면 null로 다시 바꿈
            }
            //메시지 전송 시작

            while (true){//사용자 입력
                System.out.println("원하는 옵션 숫자를 입력해주세요. 옵션 = \n1 -> [에코기능] \n2 -> [소문자로 에코] \n3 -> [대문자로 에코] \n4 -> [종료]");
                while (true){
                    try {
                        option = Integer.parseInt(userInput.readLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("숫자를 입력해주세요");
                    }
                }

                //종료사인 체크 -> 맞으면 종료
                if (option == 4){
                    out.println(protocolMapper.clientToServerMessage(new MessageDto(name,4, KeyWord.EXIT_SIGN)));
                    System.out.println("서버 측이 종료 요청을 확인할 때까지 기다려주세요.");
                    String line = null;
                    while ((line = in.readLine()) == null){
                        try {
                            Thread.sleep(300); //0.3초 마다 확인
                        } catch (InterruptedException e) {
                            break; //메시지가 왔으면
                        }
                    }
                    if (line.equals(KeyWord.EXIT_CONFIRM)){
                        System.out.println("서버 측이 연결 종료확인하였습니다. 클라이언트 채팅 프로그램을 종료합니다.");
                        System.out.println(BOUNDARY);
                        return;
                    }
                    return; //어잿든 종료
                }


                System.out.println("원하는 메시지를 입력해주세요.");
                message = userInput.readLine();



                //전송 출력
                MessageDto messageDto = new MessageDto(name, option, message);
                System.out.println(BOUNDARY);

                //프로토콜 만들어서 전송
                out.println(protocolMapper.clientToServerMessage(messageDto));
                System.out.printf("%s -> %s || [%s] : %s\n",
                        messageDto.getFrom(),
                        messageDto.getTo(),
                        messageDto.getUserName(),
                        messageDto.getMessage());

                //서버 응답 받기
                Object o = protocolMapper.readMessage(in.readLine());
                if (o instanceof ErrorMessageDto errorMessageDto){
                    //에러 DTO 일 경우
                    System.out.printf("%s -> %s || ErrorCode[%d] : %s\n",
                            errorMessageDto.getFrom(),
                            errorMessageDto.getTo(),
                            errorMessageDto.getErrorcode(),
                            errorMessageDto.getMessage());
                } else if (o instanceof MessageDto receiveMessage) {
                    System.out.printf("%s -> %s || [%s] : %s\n",
                            receiveMessage.getFrom(),
                            receiveMessage.getTo(),
                            receiveMessage.getUserName(),
                            receiveMessage.getMessage());
                }
                System.out.println(BOUNDARY);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Socket attemptConnect(int retryTime) throws ConnectionFailedException {
        for (int i = 0; i < retryTime; i++) {
            try {
                return new Socket(hostName, portNumber);
            } catch (UnknownHostException ignored) {
            } catch (IOException e){
                break; //바로 ConnectionFailed
            }
            try {Thread.sleep(1000);} catch (InterruptedException e) {} //1초 대기 후 다시 시도
        }
        //만약 지정된 횟수만큼 시도했는데 안되면,
        throw new ConnectionFailedException();
    }
}
