package org.example.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//Tomcat 같은 WAS 역할
public class TcpServer {


    //포트 넘버 상수
    private final int PORT_NUMBER;
    private static final int MAX_THREAD_POOL_SIZE = 10;

    private final ExecutorService threadPool;


    public TcpServer(int portNumber,int threadPoolSize) {
        this.PORT_NUMBER = portNumber;
        //쓰레드풀 생성
        threadPool = Executors.newFixedThreadPool(Math.min(MAX_THREAD_POOL_SIZE, threadPoolSize));
    }


    public void startServerSocket(){
        //서버 소켓 생성
        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);) {
            // 10초 마다 한번씩 인터럽트에 걸려 accept Block 에서 빠져나온다.
            serverSocket.setSoTimeout(10*1000);

            while (true){
                if (Thread.interrupted()){
                    break;
                }
                Socket socket = null;
                try {
                    socket = serverSocket.accept();//새로운 소켓 연결
                    //쓰레드 풀의 빈 쓰레드에 쓰레드 배정해서 WebApplication 실행
                    final Socket finalSocket = socket;
                    threadPool.submit(() -> {
                        WebApplication webApplication = new WebApplication();
                        webApplication.socketHandler(finalSocket); //Runtime Exception - 예상치 못한 문제 발생 가능, 기능이 다 하면 종료됨
                    });
                } catch (SocketTimeoutException e){
                } catch (IOException e){
                    if (Thread.interrupted()) break;
                }
            }
            threadPool.shutdown();
        } catch (Exception e) {
            throw new RuntimeException("소켓 생성중 예외 발생");
        } finally {
            //어떤 경우에도 쓰레드풀은 종료하고 끝낸다.
            threadPool.shutdown();
        }
        System.out.println("SYSTEM_LOG : TcpServer.java : TCP 서버 종료");
    }

    public boolean isTerminated(){
        return threadPool.isTerminated();
    }
    public void shutDownNow(){
        threadPool.shutdownNow();
    }

}
