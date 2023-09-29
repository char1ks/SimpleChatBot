package server;

import network.TCPConnection;
import network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }
    private final ArrayList<TCPConnection>connections=new ArrayList<>();
    private ChatServer(){
        StringBuilder serverRunning= new StringBuilder("Server is Running");
        try {
            for (int i = 1; i <=3; i++) {
                Thread.sleep(1000);
                serverRunning.append(".");
                System.out.println(serverRunning);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try(ServerSocket serverSocket = new ServerSocket(1000)) {
            while (true){
                try {
                    new TCPConnection(this,serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPCOnnection exception"+e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnectios("Client connected:"+tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {//Рассылка всем клиентам
        sendToAllConnectios(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnectios("Client disconnected:"+tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception:"+e);
    }
    private void sendToAllConnectios(String value){
        System.out.println(value);
        final int count = connections.size();
        for (int i = 0; i < count; i++)
            connections.get(i).sendString(value);
    }
}
