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

class LinkGame implements ActionListener {
    //判断是否有按钮被击中
    static boolean pressInformation = false;
    private AudioClip audioClip;
    Socket client;
    DataOutputStream dosOutToServer;


    //主面板
    JFrame mainFrame;

    //面板容器
    Container thisContainer;

    //子面板 
    JPanel centerPanel, southPanel, northPanel, westPanel, eastPanel;

    //游戏按钮数组
    JButton[][] diamondsButton = new JButton[Standard.columns][Standard.rows];

    //开始，退出，重列，重新开始按钮
    JButton exitButton, resetButton, newlyButton;

    // JButton startButton;
    //分数标签
    JLabel fractionLable = new JLabel("0");

    //时间标签
    JLabel time = new JLabel("");

    //分别记录两次被选中的按钮
    JButton firstButton, secondButton;

    //储存游戏按钮位置 
    int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];

    //被选中的两个游戏按钮的位置坐标(x0,y0),(x,y)
    int x0 = 0, y0 = 0, x = 0, y = 0;

    //游戏剩余时间
    int restTime = Standard.totalTime;
    int score = 0;

    //两个被选中按钮上相应的数字
    int firstMsg = 0, secondMsg = 0;
    int i, j, k , n;
    Timer t;
    Color b = new Color(243,129,129, 255);
    Color g = new Color(234,255,208, 255);
    Color m = new Color(149,225,211, 255);
    boolean flag, ifNewGame = true,
            isClientConnected = false,
            isTransport = false;

    LinkGame() {
        try {
            audioClip = Applet.newAudioClip(new URL("file:src/LinkGame/bgm/bgm.wav"));
            audioClip.loop();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //初始化
    public void init() {
        mainFrame = new JFrame("连连看对战！");
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
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //关闭窗口，结束程序
        mainFrame.setResizable(false); //设置窗口大小为不可改变
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
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT)); //设置左对齐

        JPanel panel2 = new JPanel(new GridLayout(1, 4));

        JMenuBar menubar = new JMenuBar();
        menubar.setBackground(g);

        JMenu help = new JMenu("帮助"),
              musicOn = new JMenu("音乐开"),
              musicOff = new JMenu("音乐关"),
              stop = new JMenu("暂停"),
              goOn = new JMenu("继续");
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
        stop.setFont(new Font("微软雅黑", Font.BOLD, 14));
        goOn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        musicOn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        musicOff.setFont(new Font("微软雅黑", Font.BOLD, 14));
        help.setFont(new Font("微软雅黑", Font.BOLD, 14));

        northPanel.add(panel1);
        panel1.setBackground(g);
        panel1.add(menubar);
        northPanel.add(panel2, "South");
        JLabel restTimeLabel = new JLabel("       剩余时间："),
               yourScoreLabel = new JLabel("您的得分：");
        restTimeLabel.setForeground(new Color(252,227,138, 255));
        yourScoreLabel.setForeground(new Color(252,227,138, 255));
        restTimeLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        yourScoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        time.setForeground(new Color(252,227,138, 255));
        fractionLable.setForeground(new Color(252,227,138, 255));
        panel2.add(BorderLayout.WEST, restTimeLabel);
        panel2.add(BorderLayout.EAST, time);
        time.setFont(new Font("微软雅黑", Font.BOLD, 20));
        panel2.add(BorderLayout.CENTER, yourScoreLabel);
        panel2.add(BorderLayout.EAST, fractionLable);
        panel2.add(fractionLable, "Center");
        fractionLable.setFont(new Font("微软雅黑", Font.BOLD, 20));
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
                        "点击“设置”可进行打开、关闭声音的切换\n" + "当遇到死局时，点击“重列”进行重排\n" +
                        "点击“退出”结束游戏，点击“下一局”继续游戏", " 帮助", firstMsg);
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

        //把CenterPanel区域设置为网格布局
        centerPanel.setLayout(new GridLayout(Standard.columns, Standard.rows));

        for (int cols = 0; cols < Standard.columns; cols++) {
            for (int rows = 0; rows < Standard.rows; rows++) {
                if (grid[cols + 1][rows + 1] != 0) { //给指定按钮添加图片
                    diamondsButton[cols][rows] = createImgBtn("src/LinkGame/grid/" +
                            grid[cols + 1][rows + 1] + ".png",
                            String.valueOf(grid[cols + 1][rows + 1]));
                } else { //当指定按钮为空时传空字符串即照片为传的照片为空
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
            ImageIcon icon = new ImageIcon("src/LinkGame/grid/" + firstMsg + "_chosen.png");
            firstButton.setIcon(icon);
        }

        exitButton = new JButton("退出");
        exitButton.addActionListener(this);
        resetButton = new JButton("重列");
        resetButton.addActionListener(this);
        newlyButton = new JButton("下一局");
        newlyButton.addActionListener(this);
        //  southPanel.add(startButton);
        southPanel.add(exitButton);
        southPanel.add(resetButton);
        southPanel.add(newlyButton);
        /**
        *为该标签设置一个文本字符串
        *该字符串为标签字符串所显示的文本字符串作为有符号的十进制整数为参数的字符串
        */
        fractionLable.setText(String.valueOf(score));
        mainFrame.setBounds(180, 10, 700, 700); //设置主面板的位置和大小 
                                                // mainFrame.setVisible(false);
        mainFrame.setVisible(true);
    }

    //解析状态
    public void parseState(String state) {
        // 解析打包的状态
        String[] arr0 = state.split(" ");
        // 存入图案
        String[] arr1 = arr0[0].split(",");
        int k = 0;
        for (i = 0; i < (Standard.columns + 2); i++){
            for (j = 0; j < (Standard.rows + 2); j++){
                grid[i][j] = Integer.parseInt(arr1[k]);
                k++;
            }
        }
        // 存入分数时间和点击状态
        score = Integer.parseInt(arr0[1]);
        restTime = Integer.parseInt(arr0[2]);
        String[] arr2 = arr0[3].split(",");
        x = Integer.parseInt(arr2[0]);
        y = Integer.parseInt(arr2[1]);
    }

    //创建带有图片的按钮
    public JButton createImgBtn(String ing, String txt) { //根据给定名称的资源创建一个 ImageIcon。

        ImageIcon image = new ImageIcon(ing);
        JButton button = new JButton(txt, image);
        //设置文本（即数字）相对于图标的垂直位置为底及水平位置为中心
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);

        return button;
    }

    /**
     *产生游戏中的随机数字
     *数字至少两两相同
     */
    public void randomBuild() {
        int randoms;
        int cols;
        int rows;

        for (int twins = 1; twins <= 30; twins++) {
            randoms = (int) ((Math.random() * Standard.imageNumbers) + 1); //随机产生一个1~Standard.imageNumbers的数字 

            for (int alike = 1; alike <= 2; alike++) { //产生两个随机的坐标来存放同一个数字
                cols = (int) ((Math.random() * Standard.columns) + 1);
                rows = (int) ((Math.random() * Standard.rows) + 1);

                while (grid[cols][rows] != 0) //避免出现重复的坐标
                 {
                    cols = (int) ((Math.random() * Standard.columns) + 1);
                    rows = (int) ((Math.random() * Standard.rows) + 1);
                }

                this.grid[cols][rows] = randoms;
            }
        }
    }

    //计算得分
    public void fraction() {
        score++;
        fractionLable.setText(String.valueOf(score));
        if (score == Standard.totalScore){
            t.cancel();
            JLabel win = new JLabel("游戏成功！");
            win.setForeground(Color.white);
            win.setFont(new Font("微软雅黑", Font.BOLD, 12));
            centerPanel.add(BorderLayout.CENTER, win);
        }
    }

    //重列
    public void reload() {
        int[] save = new int[Standard.columns * Standard.rows];
        int n = 0;
        int cols;
        int rows;
        int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];

        for (int i = 0; i <= Standard.columns; i++) {
            for (int j = 0; j <= Standard.rows; j++) {
                if (this.grid[i][j] != 0) { //将未消去的图片的数字放在save数组中
                    save[n] = this.grid[i][j];
                    n++;
                }
            }
        }

        n = n - 1;
        this.grid = grid; //把grid数组全部初始化为零

        while (n >= 0) { //将剩下的所有图片数字重新随机放在数组grid中
            cols = (int) ((Math.random() * Standard.columns) + 1);
            rows = (int) ((Math.random() * Standard.rows) + 1);

            while (grid[cols][rows] != 0) //避免出现重复的坐标
             {
                cols = (int) ((Math.random() * Standard.columns) + 1);
                rows = (int) ((Math.random() * Standard.rows) + 1);
            }

            this.grid[cols][rows] = save[n];
            n--;
        }

        mainFrame.setVisible(false);
        mainFrame.setResizable(false);
        //将按钮点击信息归为初始 
        pressInformation = false;
        x = 0;
        y = 0;
        init();
    }

    //选中按钮信息的存储与操作
    public void estimateEven(int placeX, int placeY, JButton bz) {
        if (pressInformation == false) { //如果第一个按钮未被击中，则将传来的按钮坐标赋给(x,y) 
            x = placeX;
            y = placeY;
            firstMsg = grid[x][y]; //将按钮上的数字赋给firstMsg
            firstButton = bz; //将(placeX，placey)位置上的按钮bz赋给firstButton
            pressInformation = true; //将按钮点击信息设置为true
            ImageIcon icon = new ImageIcon("src/LinkGame/grid/" + firstMsg + "_chosen.png");
            firstButton.setIcon(icon);
        } else {
            /**
            *如果第一个按钮被击中 ,则将第一个按钮坐标赋给(x0,y0)
            *将firstButton按钮赋给secondButton按钮
            *将传来的按钮坐标赋给坐标（x，y）
            *如果当两个不同的按钮上的数字相等时则调用remove()函数消去
            */
            x0 = x;
            y0 = y;
            secondMsg = firstMsg; //将击中的第一个按钮上的数字赋给第二个按钮
            secondButton = firstButton; //将第一个按钮赋给第二个按钮
            x = placeX;
            y = placeY;
            firstMsg = grid[x][y]; //将传过来的按钮上的数字赋给firstMsg
            firstButton = bz; //将传过来的按钮赋给firstButton

            if (secondButton == firstButton){
                pressInformation = false;
                x = 0;
                y = 0;
                ImageIcon icon = new ImageIcon("src/LinkGame/grid/" + firstMsg + ".png");
                firstButton.setIcon(icon);
            }
            else if ((firstMsg == secondMsg) && eliminate()) { //如果当两个不同的按钮上的数字相等时则消去
            }
            else {
                ImageIcon icon = new ImageIcon("src/LinkGame/grid/" + firstMsg + "_chosen.png");
                firstButton.setIcon(icon);
                icon = new ImageIcon("src/LinkGame/grid/" + secondMsg + ".png");
                secondButton.setIcon(icon);
            }
        }
    }

    //消去
    public boolean eliminate() {
        if (((x0 == x) && ((y0 == (y + 1)) || (y0 == (y - 1)))) ||
                (((x0 == (x + 1)) || (x0 == (x - 1))) && (y0 == y))) { //如果两个按钮相邻，则消去 
            remove();
            return true;
        }
        else {
            //如果两个按钮不相邻
            //判断与第一按钮同行的情况
            for (j = 0; (j < Standard.rows + 2); j++) { //判断第一个按钮同行哪个按钮为空
                if (grid[x0][j] == 0) { //如果同行有空按钮
                    if (y > j) { //如果第二个按钮的y坐标大于空按钮的j坐标说明空按钮在第二按钮左边 
                        for (i = y - 1; i >= j; i--) { //判断第二按钮左侧直到位置(x,j)有没有按钮 
                                                       //即判断与空按钮同列、与第二按钮同行的位置到第二按钮的左侧为止有没有按钮
                            if (grid[x][i] != 0) { //如果有按钮，则将k初始化为零，并将跳出循环 
                                k = 0;
                                break;
                            } else { //如果没有按钮
                                k = 1; //K=1说明通过了第一次验证  
                            }
                        }
                        if (k == 1) { //k==1说明横坐标为x,纵坐标从j到(y-1)的位置都没有按钮
                                      //即说明与空按钮同列、与第二按钮同行的位置到第二按钮的左侧为止没有按钮
                            linePassOne();
                        }
                    }
                    if (y < j) { //如果第二个按钮的y坐标小于空按钮的j坐标说明空按钮在第二按钮右边 
                        for (i = y + 1; i <= j; i++) { //判断第二按钮右侧直到位置(x,j)有没有按钮 
                            if (grid[x][i] != 0) { //如果有按钮，则将k初始化为零，并将跳出循环
                                k = 0;
                                break;
                            } else { //如果没有按钮
                                k = 1;
                            }
                        }
                        if (k == 1) { //通过第一次验证，即第二按钮右侧直到位置(x,j)没有按钮
                            linePassOne();
                        }
                    }
                    if (y == j) { //第二个按钮与空按钮同列，即第二按钮与第一按钮同行
                        linePassOne();
                    }
                }
                if (k == 2) { //通过第二验证
                    if (x0 == x) { //两个按钮在同一行
                        remove();
                        return true;
                    }
                    if (x0 < x) { //第二个按钮所在行在第一按钮所在行的下面
                        for (n = x0; n <= (x - 1); n++) { //判断空按钮下侧直到位置(x-1,j)有没有按钮
                            if (grid[n][j] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                                k = 0;
                                break;
                            }
                            if ((grid[n][j] == 0) && (n == (x - 1))) { //如果直到位置(x-1,j)没有按钮
                                remove();
                                return true;
                            }
                        }
                    }
                    if (x0 > x) { //第二个按钮所在行在第一按钮所在行的上面
                        for (n = x0; n >= (x + 1); n--) { //判断空按钮上侧直到位置(x+1,j)有没有按钮
                            if (grid[n][j] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                                k = 0;
                                break;
                            }
                            if ((grid[n][j] == 0) && (n == (x + 1))) { //如果直到位置(x+1,j)没有按钮
                                remove();
                                return true;
                            }
                        }
                    }
                }
            }
            //判断与第一按钮同列情况
            for (i = 0; i < (Standard.columns + 2); i++) { //判断第一个按钮同列哪个按钮为空
                if (grid[i][y0] == 0) { //同列有空按钮
                    if (x > i) { //如果第二个按钮的x坐标大于空按钮的i坐标说明空按钮在第二按钮上边
                        for (j = x - 1; j >= i; j--) { //判断第二按钮上侧直到位置(i,y)有没有按钮 
                            if (grid[j][y] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                                k = 0;
                                break;
                            } else { //如果没有按钮
                                k = 1; //说明通过第一次验证
                            }
                        }
                        if (k == 1) { //第二按钮上侧直到位置(i,y)没有按钮
                            rowPassOne();
                        }
                    }
                    else if (x < i) { //空按钮在第二按钮下边
                        for (j = x + 1; j <= i; j++) { //判断第二按钮下侧直到位置(i,y)有没有按钮
                            if (grid[j][y] != 0) {
                                k = 0;
                                break;
                            } else {
                                k = 1;
                            }
                        }
                        if (k == 1) { //第二按钮下侧直到位置(i,y)没有按钮
                            rowPassOne();
                        }
                    }
                    else if (x == i) { //第二按钮与空按钮同行
                        rowPassOne();
                    }
                }
                if (k == 2) { //通过第二次验证
                    if (y0 == y) { //两个按钮同列
                        remove();
                        return true;
                    }
                    if (y0 < y) { //第二按钮所在行在第一按钮所在行的下面
                        for (n = y0; n <= (y - 1); n++) { //判断空按钮右侧直到位置(i,y-1)有没有按钮
                            if (grid[i][n] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                                k = 0;
                                break;
                            }
                            if ((grid[i][n] == 0) && (n == (y - 1))) { //空按钮右侧直到位置(i,y-1)没有按钮
                                remove();
                                return true;
                            }
                        }
                    }
                    if (y0 > y) { //第二按钮所在行在第一按钮所在行的上面
                        for (n = y0; n >= (y + 1); n--) { //判断空按钮左侧直到位置(i,y+1)有没有按钮
                            if (grid[i][n] != 0) {
                                k = 0;
                                break;
                            }
                            if ((grid[i][n] == 0) && (n == (y + 1))) { //空按钮左侧直到位置(i,y+1)没有按钮
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
     * 第一按钮的同行中存在空按钮
     * 判断在同一行中空按钮与第一个按钮之间的位置是否有按钮存在，如果有则k=0,否则k=2
     * */
    public void linePassOne() {
        if (y0 > j) { //第一按钮在同行空按钮的右边 
            for (i = y0 - 1; i >= j; i--) { //判断第一按钮同左侧空按钮之间有没按钮 
                if (grid[x0][i] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                    k = 0;
                    break;
                } else { //如果没有按钮
                    k = 2; //K=2说明通过了第二次验证  
                }
            }
        }
        if (y0 < j) { //第一按钮在同行空按钮的左边 
            for (i = y0 + 1; i <= j; i++) { //判断第一按钮同右侧空按钮之间有没按钮 
                if (grid[x0][i] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                    k = 0;
                    break;
                } else {
                    k = 2;
                }
            }
        }
    }
    /**
     * 第一按钮的同列中存在空按钮
     * 判断在同一列中空按钮与第一个按钮之间的位置是否有按钮存在，如果有则k=0,否则k=2
     * */
    public void rowPassOne() {
        if (x0 > i) { //第一按钮在同列空按钮的下边
            for (j = x0 - 1; j >= i; j--) { //判断第一按钮同上侧空按钮之间有没按钮
                if (grid[j][y0] != 0) { //如果有按钮，将k初始化为零，并跳出循环
                    k = 0;
                    break;
                } else { //如果没有按钮
                    k = 2; //K=2说明通过了第二次验证 
                }
            }
        }
        if (x0 < i) { //第一按钮在同列空按钮的上边
            for (j = x0 + 1; j <= i; j++) { //判断第一按钮同下侧空按钮之间有没按钮
                if (grid[j][y0] != 0) {
                    k = 0;
                    break;
                } else {
                    k = 2;
                }
            }
        }
    }

    //将相同两个按钮消去，即设为不可见
    public void remove() {
        firstButton.setVisible(false);
        secondButton.setVisible(false);
        grid[x0][y0] = 0;
        grid[x][y] = 0;
        fraction(); //每消去一对按钮则加1分
        /**
         * 将点击按钮信息归为初始
         * 将K和被消去的两个按钮的坐标初始为零
            */
        pressInformation = false;
        x = 0;
        y = 0;
        k = 0;
    }

    //实现事件监听
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newlyButton) { //点击下一局按钮事件 
            flag = true;
            ifNewGame = true;

            int[][] grid = new int[Standard.columns + 2][Standard.rows + 2];
            this.grid = grid; //将grid数组初始化为零
            randomBuild(); //重新获取15个随机的1~Standard.imageNumbers的数字
                           //将一切信息归为初始

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

        if (e.getSource() == exitButton) { //点击退出按钮事件
            audioClip.stop(); //点击退出，终止音频播放
            if (isClientConnected){
              try{
                dosOutToServer.writeBytes("exit" + '\n');
              } catch(Exception c){}
            }
            System.exit(0);
        }

        if (e.getSource() == resetButton) { //点击重列按钮事件
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
                if (e.getSource() == diamondsButton[cols][rows]) { //当点击按钮时，调用estimateEven方法
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

    //时间提示
    public void timerDemo() {
        /**
          * schedule(TimerTask task, Date firstTime, long period)
          * 安排指定的任务在指定的时间开始进行重复的固定延迟执行
          **/
        t.schedule(new TimerTask() { //创建一个新的计时器任务

                public void run() { //此计时器任务要执行的操作

                    if (flag == true) {
                        this.cancel(); //取消此计时器任务
                        flag = false;
                    }

                    if (restTime == 0) { //时间跑完,则游戏结束
                        time.setText("很遗憾时间到！");
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
            // 上述情况不符合，只可能是对方出来新建游戏的情况
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

    // 将需要传输的信息打包为字符串
    public String packState(){
        String s = "";
        // 存入图案信息
        for (i = 0; i < (Standard.columns + 2); i++){
            for (j = 0; j < (Standard.rows + 2); j++){
                s = s + String.valueOf(grid[i][j]) + ",";
            }
        }
        // 存入分数和时间（考虑传输延迟，传输时时间减一秒）
        s += " " + String.valueOf(score) +
             " " + String.valueOf(restTime - 1) +
             " " + String.valueOf(x) + "," + String.valueOf(y);
        return s;
    }
}
