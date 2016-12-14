package LinkGame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.ws.Holder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ClientAddressFrame extends JFrame{

  int windowWidth;
  int windowHeight;
  String strIP =  "";
  int host;
  public ClientAddressFrame() {
    super("客户端启动");

    /**使窗口居中*/
    windowWidth = 310;
    windowHeight = 210;
      Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
      Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
      int screenWidth = screenSize.width;                     //获取屏幕的宽
      int screenHeight = screenSize.height;                   //获取屏幕的高
      setLocation(screenWidth / 2 - windowWidth / 2,
              screenHeight / 2 - windowHeight / 2);           //设置窗口居中显示
      setSize(windowWidth, windowHeight);

      //单击右上角X时退出程序
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      getContentPane().setLayout(null);
      
      textField = new JTextField("127.0.0.1");
      textField.setBounds(58, 56, 180, 34);
      getContentPane().add(textField);
      textField.setColumns(10);
      
      JLabel lblip = new JLabel("请输入服务器IP地址：");
      lblip.setBounds(58, 28, 160, 16);
      getContentPane().add(lblip);
      
      JButton btnNewButton = new JButton("确定");
      btnNewButton.addMouseListener(new MouseAdapter() {
       @Override
       public void mouseClicked(MouseEvent e) {
        strIP = textField.getText();
        LianLianKan llk = new LianLianKan();
        try {
          TCPServer tcpServer = new TCPServer(InetAddress.getByName(strIP), 6789);
          tcpServer.setGame(llk);
          Thread server = new Thread(tcpServer, "server");
          server.start();
          llk.createClient(InetAddress.getByName(strIP), 7890);
          llk.dosOutToServer.writeBytes("client" + '\n');
        }catch (Exception c){}
        dispose();
      }
    });
      btnNewButton.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
       }
     });
      btnNewButton.setBounds(93, 110, 99, 34);
      getContentPane().add(btnNewButton);
      //显示
      setVisible(true);
    }

    private JTextField textField;
  }