package LinkGame;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.lang.*;
import org.omg.PortableInterceptor.INACTIVE;


public class TCPServer extends Thread{
  private LianLianKan game;
  InetAddress ip;
  int host;
  boolean flag = true; // ���ڼ�¼����˵Ŀͻ����Ƿ��ѿ���
  public void setGame(LianLianKan game){
    this.game = game;
  }

  TCPServer(InetAddress inIp, int inHost){
    ip = inIp;
    host = inHost;
  }

  @Override
  public void run(){
    String strSocket, strLocal;
    try {
      ServerSocket ssocketWelcome = new ServerSocket(host);
      while(true) {
        Socket socketServer = ssocketWelcome.accept();
        BufferedReader brInFromClient = new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
        do{
          strSocket = brInFromClient.readLine();
          if (!flag){
            game.tcpServer(strSocket);
          }
          else{
            // �ͻ��˵�һ�η�����Ϣ
            flag = false;
            if (strSocket.equals("client")){
              game.createClient(ip, 6789);
              game.dosOutToServer.writeBytes(game.packState() + '\n');
            }
            else {
              game.parseState(strSocket);
              game.init();
            }
          }
        } while(true);
    }
   }
   catch (Exception e){
   }
  }
}