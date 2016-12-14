package LinkGame;

import javax.xml.ws.Holder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.applet.Applet;
import java.applet.AudioClip;
import org.omg.CORBA.PUBLIC_MEMBER;

public class ServerAddressFrame extends JFrame{
  int windowWidth;
  int windowHeight;
  String strIP =  "";
  int host;
  public ServerAddressFrame() {
    super("����������");

    /**ʹ���ھ���*/
    windowWidth = 310;
    windowHeight = 210;
    Toolkit kit = Toolkit.getDefaultToolkit();              //���幤�߰�
    Dimension screenSize = kit.getScreenSize();             //��ȡ��Ļ�ĳߴ�
    int screenWidth = screenSize.width;                     //��ȡ��Ļ�Ŀ�
    int screenHeight = screenSize.height;                   //��ȡ��Ļ�ĸ�
    setLocation(screenWidth / 2 - windowWidth / 2,
            screenHeight / 2 - windowHeight / 2);           //���ô��ھ�����ʾ
    setSize(windowWidth, windowHeight);

    //�������Ͻ�Xʱ�˳�����
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(null);
    
    textField = new JTextField("127.0.0.1");
    textField.setBounds(58, 56, 180, 34);
    getContentPane().add(textField);
    textField.setColumns(10);
    
    JLabel lblip = new JLabel("������ͻ���IP��ַ��");
    lblip.setBounds(58, 28, 160, 16);
    getContentPane().add(lblip);
    
    JButton btnNewButton = new JButton("ȷ��");
    btnNewButton.addMouseListener(new MouseAdapter() {
     @Override
     public void mouseClicked(MouseEvent e) {
      strIP = textField.getText();
      LianLianKan llk = new LianLianKan();
      try {
        TCPServer tcpServer = new TCPServer(InetAddress.getByName(strIP), 7890);
        tcpServer.setGame(llk);
        Thread server = new Thread(tcpServer, "server");
        server.start();
      }
      catch (Exception c){
      }
      llk.randomBuild();
      llk.init();
      dispose();
    }
  });
    btnNewButton.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
     }
   });
    btnNewButton.setBounds(93, 110, 99, 34);
    getContentPane().add(btnNewButton);
    //��ʾ
    setVisible(true);
  }

  private JTextField textField;
}