package org.example;

import org.example.Server.ServerThread;
import org.example.client.TcpClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {



        //다른 쓰레드에서 서버 생성 - 내부적으로 쓰레드 풀을 만들어 요청하나당 하나씩 배정하고, 그 안에서 WebApplication 실행함
        Thread serverThread = new ServerThread();
        serverThread.setDaemon(true); //안전하게 데몬쓰레드로
        serverThread.start();


        //클라이언트 생성
        TcpClient tcpClient = new TcpClient("localhost",12345);
        tcpClient.startChatting(10); //서버 연결 안될시 1초간격으로 10번 시도 하세요

        //서버 쓰레드에게 인터럽트를 보냅니다.
        //데몬 쓰레드 이긴 하지만, 내부에 쓰레드 풀이 있으므로, 안전하게 인터럽트를 보냅니다.
        serverThread.interrupt();

        try {
            serverThread.join(); //다 완료될때까지 main 쓰레드 대기
        } catch (InterruptedException e) {
            //다 완료됨
        }

        System.out.println("SYSTEM_LOG : Main.java : 웹서버와 클라이언트가 정상적으로 종료되었습니다.");
        //프로그램 종료
        System.out.println("SYSTEM_LOG : Main.java : 메인쓰레드 종료");
    }
}