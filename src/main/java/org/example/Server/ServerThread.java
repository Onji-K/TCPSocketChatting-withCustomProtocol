package org.example.Server;

import java.io.IOException;

public class ServerThread extends Thread{



    @Override
    public void run() {
        //WAS 생성 - 내부적으로 쓰레드 풀을 만들어 요청하나당 하나씩 배정하고, 그 안에서 WebApplication 실행함
        TcpServer tcpServer = new TcpServer(12345,10); //쓰레드 풀 10개짜리 웹서버 생성
        tcpServer.startServerSocket(); //웹서버 시작
        //서버 종료됨

        //쓰레드풀 종료되었는지 확인
        for (int i = 0; i<5;i++){
            if (tcpServer.isTerminated()) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        if (!tcpServer.isTerminated()){
            //종료 안됬으면
            tcpServer.shutDownNow();
        }
        //서버 종료

    }
}
