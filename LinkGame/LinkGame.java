package LinkGame;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

final class Standard {
    public final static int rows = 10;
    public final static int columns = 6;
    public final static int totalTime = 60;
    public final static int imageNumbers = 24;
    public final static int totalScore = rows * columns / 2;
}

class LianLianKan implements ActionListener {
    //�ж��Ƿ��а�ť������
    static boolean pressInformation = false;
    private AudioClip audioClip;
    Socket client;
    DataOutputStream dosOutToServer;


    //�����
    JFrame mainFrame;

    //�������
    Container thisContainer;

    //����� 
    JPanel centerPanel, southPanel, northPanel, westPanel, eastPanel;

    //��Ϸ��ť����
    JButton[][] diamondsButton = new JButton[Standard.columns][Standard.rows];

    //��ʼ���˳������У����¿�ʼ��ť
    JButton exitButton, resetButton, newlyButton;

    // JButton startButton;
    //������ǩ
    JLabel fractionLable = new JLabel("0");

    //ʱ���ǩ
    JLabel time = new JLabel("");

    //�ֱ��¼���α�ѡ�еİ�ť
    JButton firstButton, secondButton;

    //������Ϸ��ťλ�� 
    int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];

    //��ѡ�е�������Ϸ��ť��λ������(x0,y0),(x,y)
    int x0 = 0, y0 = 0, x = 0, y = 0;

    //��Ϸʣ��ʱ��
    int restTime = Standard.totalTime;
    int score = 0;

    //������ѡ�а�ť����Ӧ������
    int firstMsg = 0, secondMsg = 0;
    int i, j, k , n;
    Timer t;
    Color b = new Color(243,129,129, 255);
    Color g = new Color(234,255,208, 255);
    Color m = new Color(149,225,211, 255);
    boolean flag, ifNewGame = true,
            isClientConnected = false,
            isTransport = false;

    LianLianKan() {
        try {
            audioClip = Applet.newAudioClip(new URL("file:./bgm.wav"));
            audioClip.loop();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //��ʼ��
    public void init() {
        mainFrame = new JFrame("��������ս��");
        if (isTransport){
            isTransport = false;
            t = new Timer();
            timerDemo();
        }
        else if (ifNewGame){
            restTime = Standard.totalTime;
            score = 0;
            t = new Timer();
            timerDemo();
        }
        thisContainer = mainFrame.getContentPane();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //�رմ��ڣ���������
        mainFrame.setResizable(false); //���ô��ڴ�СΪ���ɸı�
                                       //thisContainer.setBackground(c);

        thisContainer.setLayout(new BorderLayout());
        centerPanel = new JPanel();
        centerPanel.setBackground(b);
        southPanel = new JPanel();
        southPanel.setBackground(g);
        northPanel = new JPanel(new GridLayout(2, 0));
        northPanel.setBackground(m);
        westPanel = new JPanel();
        westPanel.setBackground(m);
        eastPanel = new JPanel();
        eastPanel.setBackground(m);
        thisContainer.add(centerPanel, "Center");
        thisContainer.add(southPanel, "South");
        thisContainer.add(northPanel, "North");

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT)); //���������

        JPanel panel2 = new JPanel(new GridLayout(1, 4));

        JMenuBar menubar = new JMenuBar();
        menubar.setBackground(g);

        JMenu help = new JMenu("����"),
              musicOn = new JMenu("���ֿ�"),
              musicOff = new JMenu("���ֹ�"),
              stop = new JMenu("��ͣ"),
              goOn = new JMenu("����");
        menubar.add(stop);
        menubar.add(goOn);
        menubar.add(musicOn);
        menubar.add(musicOff);
        menubar.add(help);
        stop.setForeground(Color.white);
        goOn.setForeground(Color.white);
        help.setForeground(Color.white);
        musicOn.setForeground(Color.white);
        musicOff.setForeground(Color.white);
        stop.setFont(new Font("΢���ź�", Font.BOLD, 14));
        goOn.setFont(new Font("΢���ź�", Font.BOLD, 14));
        musicOn.setFont(new Font("΢���ź�", Font.BOLD, 14));
        musicOff.setFont(new Font("΢���ź�", Font.BOLD, 14));
        help.setFont(new Font("΢���ź�", Font.BOLD, 14));

        northPanel.add(panel1);
        panel1.setBackground(g);
        panel1.add(menubar);
        northPanel.add(panel2, "South");
        JLabel restTimeLabel = new JLabel("       ʣ��ʱ�䣺"),
               yourScoreLabel = new JLabel("���ĵ÷֣�");
        restTimeLabel.setForeground(new Color(252,227,138, 255));
        yourScoreLabel.setForeground(new Color(252,227,138, 255));
        restTimeLabel.setFont(new Font("΢���ź�", Font.BOLD, 20));
        yourScoreLabel.setFont(new Font("΢���ź�", Font.BOLD, 20));
        time.setForeground(new Color(252,227,138, 255));
        fractionLable.setForeground(new Color(252,227,138, 255));
        panel2.add(BorderLayout.WEST, restTimeLabel);
        panel2.add(BorderLayout.EAST, time);
        time.setFont(new Font("΢���ź�", Font.BOLD, 20));
        panel2.add(BorderLayout.CENTER, yourScoreLabel);
        panel2.add(BorderLayout.EAST, fractionLable);
        panel2.add(fractionLable, "Center");
        fractionLable.setFont(new Font("΢���ź�", Font.BOLD, 20));
        panel2.setBackground(m);

        stop.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                    if (isClientConnected){
                        try{
                            dosOutToServer.writeBytes("stop" + '\n');
                        } catch(Exception c){}
                    }
                    t.cancel();
                    for (int i = 0; i < Standard.columns; i++) {
                            for (int j = 0; j < Standard.rows; j++) {
                                if (grid[i + 1][j + 1] != 0) {
                                    diamondsButton[i][j].setEnabled(false);
                                }
                            }
                        }
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        goOn.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                    if (isClientConnected){
                        try{
                            dosOutToServer.writeBytes("goon" + '\n');
                        } catch(Exception c){}
                    }
                    t = new Timer();
                    timerDemo();
                    for (int i = 0; i < Standard.columns; i++) {
                            for (int j = 0; j < Standard.rows; j++) {
                                if (grid[i + 1][j + 1] != 0) {
                                    diamondsButton[i][j].setEnabled(true);
                                }
                            }
                        }
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        musicOn.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                    audioClip.loop();
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        musicOff.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                    audioClip.stop();
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        help.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                    JOptionPane.showMessageDialog(null,
                        "��������á��ɽ��д򿪡��ر��������л�\n" + "����������ʱ����������С���������\n" +
                        "������˳���������Ϸ���������һ�֡�������Ϸ", " ����", firstMsg);
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        //��CenterPanel��������Ϊ���񲼾�
        centerPanel.setLayout(new GridLayout(Standard.columns, Standard.rows));

        for (int cols = 0; cols < Standard.columns; cols++) {
            for (int rows = 0; rows < Standard.rows; rows++) {
                if (grid[cols + 1][rows + 1] != 0) { //��ָ����ť���ͼƬ
                    diamondsButton[cols][rows] = createImgBtn("./grid/" +
                            grid[cols + 1][rows + 1] + ".png",
                            String.valueOf(grid[cols + 1][rows + 1]));
                } else { //��ָ����ťΪ��ʱ�����ַ�������ƬΪ������ƬΪ��
                    diamondsButton[cols][rows] = createImgBtn("",
                            String.valueOf(grid[cols + 1][rows + 1]));
                    diamondsButton[cols][rows].setVisible(false);
                }

                diamondsButton[cols][rows].addActionListener(this);
                centerPanel.add(diamondsButton[cols][rows]);
            }
        }

        if (x != 0 && y !=0){
            pressInformation = true;
            JButton firstButton = diamondsButton[x - 1][y - 1];
            firstMsg = grid[x][y];
            ImageIcon icon = new ImageIcon("./grid/" + firstMsg + "_chosen.png");
            firstButton.setIcon(icon);
        }

        exitButton = new JButton("�˳�");
        exitButton.addActionListener(this);
        resetButton = new JButton("����");
        resetButton.addActionListener(this);
        newlyButton = new JButton("��һ��");
        newlyButton.addActionListener(this);
        //  southPanel.add(startButton);
        southPanel.add(exitButton);
        southPanel.add(resetButton);
        southPanel.add(newlyButton);
        /**
        *Ϊ�ñ�ǩ����һ���ı��ַ���
        *���ַ���Ϊ��ǩ�ַ�������ʾ���ı��ַ�����Ϊ�з��ŵ�ʮ��������Ϊ�������ַ���
        */
        fractionLable.setText(String.valueOf(score));
        mainFrame.setBounds(180, 10, 700, 700); //����������λ�úʹ�С 
                                                // mainFrame.setVisible(false);
        mainFrame.setVisible(true);
    }

    //����״̬
    public void parseState(String state) {
        // ���������״̬
        String[] arr0 = state.split(" ");
        // ����ͼ��
        String[] arr1 = arr0[0].split(",");
        int k = 0;
        for (i = 0; i < (Standard.columns + 2); i++){
            for (j = 0; j < (Standard.rows + 2); j++){
                grid[i][j] = Integer.parseInt(arr1[k]);
                k++;
            }
        }
        // �������ʱ��͵��״̬
        score = Integer.parseInt(arr0[1]);
        restTime = Integer.parseInt(arr0[2]);
        String[] arr2 = arr0[3].split(",");
        x = Integer.parseInt(arr2[0]);
        y = Integer.parseInt(arr2[1]);
    }

    //��������ͼƬ�İ�ť
    public JButton createImgBtn(String ing, String txt) { //���ݸ������Ƶ���Դ����һ�� ImageIcon��

        ImageIcon image = new ImageIcon(getClass().getResource(ing));
        JButton button = new JButton(txt, image);
        //�����ı��������֣������ͼ��Ĵ�ֱλ��Ϊ�׼�ˮƽλ��Ϊ����
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);

        return button;
    }

    /**
     *������Ϸ�е��������
     *��������������ͬ
     */
    public void randomBuild() {
        int randoms;
        int cols;
        int rows;

        for (int twins = 1; twins <= 30; twins++) {
            randoms = (int) ((Math.random() * Standard.imageNumbers) + 1); //�������һ��1~Standard.imageNumbers������ 

            for (int alike = 1; alike <= 2; alike++) { //����������������������ͬһ������
                cols = (int) ((Math.random() * Standard.columns) + 1);
                rows = (int) ((Math.random() * Standard.rows) + 1);

                while (grid[cols][rows] != 0) //��������ظ�������
                 {
                    cols = (int) ((Math.random() * Standard.columns) + 1);
                    rows = (int) ((Math.random() * Standard.rows) + 1);
                }

                this.grid[cols][rows] = randoms;
            }
        }
    }

    //����÷�
    public void fraction() {
        score++;
        fractionLable.setText(String.valueOf(score));
        if (score == Standard.totalScore){
            t.cancel();
            JLabel win = new JLabel("��Ϸ�ɹ���");
            win.setForeground(Color.white);
            win.setFont(new Font("΢���ź�", Font.BOLD, 12));
            centerPanel.add(BorderLayout.CENTER, win);
        }
    }

    //����
    public void reload() {
        int[] save = new int[Standard.columns * Standard.rows];
        int n = 0;
        int cols;
        int rows;
        int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];

        for (int i = 0; i <= Standard.columns; i++) {
            for (int j = 0; j <= Standard.rows; j++) {
                if (this.grid[i][j] != 0) { //��δ��ȥ��ͼƬ�����ַ���save������
                    save[n] = this.grid[i][j];
                    n++;
                }
            }
        }

        n = n - 1;
        this.grid = grid; //��grid����ȫ����ʼ��Ϊ��

        while (n >= 0) { //��ʣ�µ�����ͼƬ�������������������grid��
            cols = (int) ((Math.random() * Standard.columns) + 1);
            rows = (int) ((Math.random() * Standard.rows) + 1);

            while (grid[cols][rows] != 0) //��������ظ�������
             {
                cols = (int) ((Math.random() * Standard.columns) + 1);
                rows = (int) ((Math.random() * Standard.rows) + 1);
            }

            this.grid[cols][rows] = save[n];
            n--;
        }

        mainFrame.setVisible(false);
        mainFrame.setResizable(false);
        //����ť�����Ϣ��Ϊ��ʼ 
        pressInformation = false;
        x = 0;
        y = 0;
        init();
    }

    //ѡ�а�ť��Ϣ�Ĵ洢�����
    public void estimateEven(int placeX, int placeY, JButton bz) {
        if (pressInformation == false) { //�����һ����ťδ�����У��򽫴����İ�ť���긳��(x,y) 
            x = placeX;
            y = placeY;
            firstMsg = grid[x][y]; //����ť�ϵ����ָ���firstMsg
            firstButton = bz; //��(placeX��placey)λ���ϵİ�ťbz����firstButton
            pressInformation = true; //����ť�����Ϣ����Ϊtrue
            ImageIcon icon = new ImageIcon("./grid/" + firstMsg + "_chosen.png");
            firstButton.setIcon(icon);
        } else {
            /**
            *�����һ����ť������ ,�򽫵�һ����ť���긳��(x0,y0)
            *��firstButton��ť����secondButton��ť
            *�������İ�ť���긳�����꣨x��y��
            *�����������ͬ�İ�ť�ϵ��������ʱ�����remove()������ȥ
            */
            x0 = x;
            y0 = y;
            secondMsg = firstMsg; //�����еĵ�һ����ť�ϵ����ָ����ڶ�����ť
            secondButton = firstButton; //����һ����ť�����ڶ�����ť
            x = placeX;
            y = placeY;
            firstMsg = grid[x][y]; //���������İ�ť�ϵ����ָ���firstMsg
            firstButton = bz; //���������İ�ť����firstButton

            if (secondButton == firstButton){
                pressInformation = false;
                x = 0;
                y = 0;
                ImageIcon icon = new ImageIcon("./grid/" + firstMsg + ".png");
                firstButton.setIcon(icon);
            }
            else if ((firstMsg == secondMsg) && eliminate()) { //�����������ͬ�İ�ť�ϵ��������ʱ����ȥ
            }
            else {
                ImageIcon icon = new ImageIcon("./grid/" + firstMsg + "_chosen.png");
                firstButton.setIcon(icon);
                icon = new ImageIcon("./grid/" + secondMsg + ".png");
                secondButton.setIcon(icon);
            }
        }
    }

    //��ȥ
    public boolean eliminate() {
        if (((x0 == x) && ((y0 == (y + 1)) || (y0 == (y - 1)))) ||
                (((x0 == (x + 1)) || (x0 == (x - 1))) && (y0 == y))) { //���������ť���ڣ�����ȥ 
            remove();
            return true;
        }
        else {
            //���������ť������
            //�ж����һ��ťͬ�е����
            for (j = 0; (j < Standard.rows + 2); j++) { //�жϵ�һ����ťͬ���ĸ���ťΪ��
                if (grid[x0][j] == 0) { //���ͬ���пհ�ť
                    if (y > j) { //����ڶ�����ť��y������ڿհ�ť��j����˵���հ�ť�ڵڶ���ť��� 
                        for (i = y - 1; i >= j; i--) { //�жϵڶ���ť���ֱ��λ��(x,j)��û�а�ť 
                                                       //���ж���հ�ťͬ�С���ڶ���ťͬ�е�λ�õ��ڶ���ť�����Ϊֹ��û�а�ť
                            if (grid[x][i] != 0) { //����а�ť����k��ʼ��Ϊ�㣬��������ѭ�� 
                                k = 0;
                                break;
                            } else { //���û�а�ť
                                k = 1; //K=1˵��ͨ���˵�һ����֤  
                            }
                        }
                        if (k == 1) { //k==1˵��������Ϊx,�������j��(y-1)��λ�ö�û�а�ť
                                      //��˵����հ�ťͬ�С���ڶ���ťͬ�е�λ�õ��ڶ���ť�����Ϊֹû�а�ť
                            linePassOne();
                        }
                    }
                    if (y < j) { //����ڶ�����ť��y����С�ڿհ�ť��j����˵���հ�ť�ڵڶ���ť�ұ� 
                        for (i = y + 1; i <= j; i++) { //�жϵڶ���ť�Ҳ�ֱ��λ��(x,j)��û�а�ť 
                            if (grid[x][i] != 0) { //����а�ť����k��ʼ��Ϊ�㣬��������ѭ��
                                k = 0;
                                break;
                            } else { //���û�а�ť
                                k = 1;
                            }
                        }
                        if (k == 1) { //ͨ����һ����֤�����ڶ���ť�Ҳ�ֱ��λ��(x,j)û�а�ť
                            linePassOne();
                        }
                    }
                    if (y == j) { //�ڶ�����ť��հ�ťͬ�У����ڶ���ť���һ��ťͬ��
                        linePassOne();
                    }
                }
                if (k == 2) { //ͨ���ڶ���֤
                    if (x0 == x) { //������ť��ͬһ��
                        remove();
                        return true;
                    }
                    if (x0 < x) { //�ڶ�����ť�������ڵ�һ��ť�����е�����
                        for (n = x0; n <= (x - 1); n++) { //�жϿհ�ť�²�ֱ��λ��(x-1,j)��û�а�ť
                            if (grid[n][j] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                                k = 0;
                                break;
                            }
                            if ((grid[n][j] == 0) && (n == (x - 1))) { //���ֱ��λ��(x-1,j)û�а�ť
                                remove();
                                return true;
                            }
                        }
                    }
                    if (x0 > x) { //�ڶ�����ť�������ڵ�һ��ť�����е�����
                        for (n = x0; n >= (x + 1); n--) { //�жϿհ�ť�ϲ�ֱ��λ��(x+1,j)��û�а�ť
                            if (grid[n][j] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                                k = 0;
                                break;
                            }
                            if ((grid[n][j] == 0) && (n == (x + 1))) { //���ֱ��λ��(x+1,j)û�а�ť
                                remove();
                                return true;
                            }
                        }
                    }
                }
            }
            //�ж����һ��ťͬ�����
            for (i = 0; i < (Standard.columns + 2); i++) { //�жϵ�һ����ťͬ���ĸ���ťΪ��
                if (grid[i][y0] == 0) { //ͬ���пհ�ť
                    if (x > i) { //����ڶ�����ť��x������ڿհ�ť��i����˵���հ�ť�ڵڶ���ť�ϱ�
                        for (j = x - 1; j >= i; j--) { //�жϵڶ���ť�ϲ�ֱ��λ��(i,y)��û�а�ť 
                            if (grid[j][y] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                                k = 0;
                                break;
                            } else { //���û�а�ť
                                k = 1; //˵��ͨ����һ����֤
                            }
                        }
                        if (k == 1) { //�ڶ���ť�ϲ�ֱ��λ��(i,y)û�а�ť
                            rowPassOne();
                        }
                    }
                    else if (x < i) { //�հ�ť�ڵڶ���ť�±�
                        for (j = x + 1; j <= i; j++) { //�жϵڶ���ť�²�ֱ��λ��(i,y)��û�а�ť
                            if (grid[j][y] != 0) {
                                k = 0;
                                break;
                            } else {
                                k = 1;
                            }
                        }
                        if (k == 1) { //�ڶ���ť�²�ֱ��λ��(i,y)û�а�ť
                            rowPassOne();
                        }
                    }
                    else if (x == i) { //�ڶ���ť��հ�ťͬ��
                        rowPassOne();
                    }
                }
                if (k == 2) { //ͨ���ڶ�����֤
                    if (y0 == y) { //������ťͬ��
                        remove();
                        return true;
                    }
                    if (y0 < y) { //�ڶ���ť�������ڵ�һ��ť�����е�����
                        for (n = y0; n <= (y - 1); n++) { //�жϿհ�ť�Ҳ�ֱ��λ��(i,y-1)��û�а�ť
                            if (grid[i][n] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                                k = 0;
                                break;
                            }
                            if ((grid[i][n] == 0) && (n == (y - 1))) { //�հ�ť�Ҳ�ֱ��λ��(i,y-1)û�а�ť
                                remove();
                                return true;
                            }
                        }
                    }
                    if (y0 > y) { //�ڶ���ť�������ڵ�һ��ť�����е�����
                        for (n = y0; n >= (y + 1); n--) { //�жϿհ�ť���ֱ��λ��(i,y+1)��û�а�ť
                            if (grid[i][n] != 0) {
                                k = 0;
                                break;
                            }
                            if ((grid[i][n] == 0) && (n == (y + 1))) { //�հ�ť���ֱ��λ��(i,y+1)û�а�ť
                                remove();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * ��һ��ť��ͬ���д��ڿհ�ť
     * �ж���ͬһ���пհ�ť���һ����ť֮���λ���Ƿ��а�ť���ڣ��������k=0,����k=2
     * */
    public void linePassOne() {
        if (y0 > j) { //��һ��ť��ͬ�пհ�ť���ұ� 
            for (i = y0 - 1; i >= j; i--) { //�жϵ�һ��ťͬ���հ�ť֮����û��ť 
                if (grid[x0][i] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                    k = 0;
                    break;
                } else { //���û�а�ť
                    k = 2; //K=2˵��ͨ���˵ڶ�����֤  
                }
            }
        }
        if (y0 < j) { //��һ��ť��ͬ�пհ�ť����� 
            for (i = y0 + 1; i <= j; i++) { //�жϵ�һ��ťͬ�Ҳ�հ�ť֮����û��ť 
                if (grid[x0][i] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                    k = 0;
                    break;
                } else {
                    k = 2;
                }
            }
        }
    }
    /**
     * ��һ��ť��ͬ���д��ڿհ�ť
     * �ж���ͬһ���пհ�ť���һ����ť֮���λ���Ƿ��а�ť���ڣ��������k=0,����k=2
     * */
    public void rowPassOne() {
        if (x0 > i) { //��һ��ť��ͬ�пհ�ť���±�
            for (j = x0 - 1; j >= i; j--) { //�жϵ�һ��ťͬ�ϲ�հ�ť֮����û��ť
                if (grid[j][y0] != 0) { //����а�ť����k��ʼ��Ϊ�㣬������ѭ��
                    k = 0;
                    break;
                } else { //���û�а�ť
                    k = 2; //K=2˵��ͨ���˵ڶ�����֤ 
                }
            }
        }
        if (x0 < i) { //��һ��ť��ͬ�пհ�ť���ϱ�
            for (j = x0 + 1; j <= i; j++) { //�жϵ�һ��ťͬ�²�հ�ť֮����û��ť
                if (grid[j][y0] != 0) {
                    k = 0;
                    break;
                } else {
                    k = 2;
                }
            }
        }
    }

    //����ͬ������ť��ȥ������Ϊ���ɼ�
    public void remove() {
        firstButton.setVisible(false);
        secondButton.setVisible(false);
        grid[x0][y0] = 0;
        grid[x][y] = 0;
        fraction(); //ÿ��ȥһ�԰�ť���1��
        /**
         * �������ť��Ϣ��Ϊ��ʼ
         * ��K�ͱ���ȥ��������ť�������ʼΪ��
            */
        pressInformation = false;
        x = 0;
        y = 0;
        k = 0;
    }

    //ʵ���¼�����
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newlyButton) { //�����һ�ְ�ť�¼� 
            flag = true;
            ifNewGame = true;

            int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];
            this.grid = grid; //��grid�����ʼ��Ϊ��
            randomBuild(); //���»�ȡ15�������1~Standard.imageNumbers������
                           //��һ����Ϣ��Ϊ��ʼ

            mainFrame.setVisible(false);
            pressInformation = false;
            fractionLable.setText("0");
            t.cancel();
            init();
            if (isClientConnected){
                try{
                    dosOutToServer.writeBytes(packState() + '\n');
                } catch(Exception c){}
            }
            timerDemo();
        }

        if (e.getSource() == exitButton) { //����˳���ť�¼�
            audioClip.stop(); //����˳�����ֹ��Ƶ����
            if (isClientConnected){
              try{
                dosOutToServer.writeBytes("exit" + '\n');
              } catch(Exception c){}
            }
            System.exit(0);
        }

        if (e.getSource() == resetButton) { //������а�ť�¼�
            ifNewGame = false;
            reload();
            if (isClientConnected){
                try {
                    dosOutToServer.writeBytes("reload:" + packState() + '\n');
                } catch (Exception c){}
            }
        }

        for (int cols = 0; cols < Standard.columns; cols++) {
            for (int rows = 0; rows < Standard.rows; rows++) {
                if (e.getSource() == diamondsButton[cols][rows]) { //�������ťʱ������estimateEven����
                    if (isClientConnected){
                        try {
                            dosOutToServer.writeBytes("click " + cols + " " + rows + '\n');
                        } catch (Exception c){}
                    }
                    estimateEven(cols + 1, rows + 1, diamondsButton[cols][rows]);
                }
            }
        }
    }

    //ʱ����ʾ
    public void timerDemo() {
        /**
          * schedule(TimerTask task, Date firstTime, long period)
          * ����ָ����������ָ����ʱ�俪ʼ�����ظ��Ĺ̶��ӳ�ִ��
          **/
        t.schedule(new TimerTask() { //����һ���µļ�ʱ������

                public void run() { //�˼�ʱ������Ҫִ�еĲ���

                    if (flag == true) {
                        this.cancel(); //ȡ���˼�ʱ������
                        flag = false;
                    }

                    if (restTime == 0) { //ʱ������,����Ϸ����
                        time.setText("���ź�ʱ�䵽��");
                        t.cancel();
                        for (int i = 0; i < Standard.columns; i++) {
                            for (int j = 0; j < Standard.rows; j++) {
                                if (grid[i + 1][j + 1] != 0) {
                                    diamondsButton[i][j].setVisible(false);
                                }
                            }
                        }
                        resetButton.setVisible(false);
                    } else {
                        time.setText("" + restTime--);
                    }
                }
            }, 1000, 1000);
    }

    public synchronized void tcpServer(String msg) {
        System.out.println(msg);
        if (msg.equals("stop")){
            t.cancel();
            for (int i = 0; i < Standard.columns; i++) {
                for (int j = 0; j < Standard.rows; j++) {
                    if (grid[i + 1][j + 1] != 0) {
                        diamondsButton[i][j].setEnabled(false);
                    }
                }
            }
        }
        else if (msg.equals("goon")){
            t = new Timer();
            timerDemo();
            for (int i = 0; i < Standard.columns; i++) {
                for (int j = 0; j < Standard.rows; j++) {
                    if (grid[i + 1][j + 1] != 0) {
                        diamondsButton[i][j].setEnabled(true);
                    }
                }
            }
        }
        else if (msg.equals("exit")){
            audioClip.stop();
            System.exit(0);
        }
        else if (msg.split(":")[0].equals("reload")){
            mainFrame.setVisible(false);
            mainFrame.setResizable(false);
            parseState(msg.split(":")[1]);
            isTransport = true;
            t.cancel();
            init();
        }
        else if (msg.split(" ")[0].equals("click")){
            int cols = Integer.parseInt(msg.split(" ")[1]),
                rows = Integer.parseInt(msg.split(" ")[2]);
            estimateEven(cols + 1, rows + 1, diamondsButton[cols][rows]);
        }
        else{
            // ������������ϣ�ֻ�����ǶԷ������½���Ϸ�����
            mainFrame.setVisible(false);
            mainFrame.setResizable(false);
            isTransport = true;
            parseState(msg);
            t.cancel();
            init();
        }
    }

    public synchronized void createClient(InetAddress ip, int host){
        isClientConnected = true;
        try {
          client = new Socket(ip, host);
          dosOutToServer = new DataOutputStream(client.getOutputStream()); 
        } catch(Exception c){}
    }

    // ����Ҫ�������Ϣ���Ϊ�ַ���
    public String packState(){
        String s = "";
        // ����ͼ����Ϣ
        for (i = 0; i < (Standard.columns + 2); i++){
            for (j = 0; j < (Standard.rows + 2); j++){
                s = s + String.valueOf(grid[i][j]) + ",";
            }
        }
        // ���������ʱ�䣨���Ǵ����ӳ٣�����ʱʱ���һ�룩
        s += " " + String.valueOf(score) +
             " " + String.valueOf(restTime - 1) +
             " " + String.valueOf(x) + "," + String.valueOf(y);
        return s;
    }
}
