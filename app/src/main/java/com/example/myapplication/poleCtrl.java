package com.example.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


public class poleCtrl extends AppCompatActivity {
    private ImageButton BT_left, BT_right, BT_backword, BT_stop, BT_forword, BT_position;
    public static String IP = "";
    public static int Port = 80;
    private Socket socket;

    private Constants order_go = null;
    private static OutputStream outputStream;
    public int ClickButton = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole_ctrl);
        BT_left = (ImageButton) findViewById(R.id.BT_LEFT);
        BT_right = (ImageButton) findViewById(R.id.BT_RIGHT);
        BT_forword = (ImageButton) findViewById(R.id.BT_FORW);
        BT_backword = (ImageButton) findViewById(R.id.BT_BACK);
        BT_stop = (ImageButton) findViewById(R.id.BT_STOP);
        BT_position = (ImageButton) findViewById(R.id.BT_Position);


        try {//得到所连接AP的IP地址
            WifiManager wifiManager = (WifiManager) this.getSystemService(poleCtrl.WIFI_SERVICE);
            DhcpInfo dhcpinfo = wifiManager.getDhcpInfo();
            IP = intToIp(dhcpinfo.serverAddress);
        }catch(Exception E){
            E.printStackTrace();
        }
        //开启子线程 连接socket
        new Thread(new Runnable(){
            @Override
            public void run() {
                Looper.prepare();
                try {
                    // 1、创建连接
                    socket = new Socket(IP, Port);
                    // 2、设置读流的超时时间
                    socket.setSoTimeout(8000);
                    // 3、获取输出流
                    outputStream = socket.getOutputStream();
                    if (!socket.isConnected()){
                        Toast.makeText(getApplicationContext(), "socket成功连接！", Toast.LENGTH_SHORT).show();
                    }
                    sendMes("N P|");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();




        //按钮控制输出相应指令
        BT_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMes(order_go.LEFT);
                ClickButton  = 3;
                setUI();
            }
        });

        BT_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMes(order_go.RIGHT);
                ClickButton  = 4;
                setUI();
            }
        });

        BT_forword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMes(order_go.FORWARD);
                ClickButton = 1;
                setUI();
            }
        });

        BT_backword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMes(order_go.BACK);
                ClickButton = 2;
                setUI();
            }
        });

        BT_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMes(order_go.STOP);
                ClickButton = 0;
                setUI();
            }
        });
        BT_position.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder position_Edit = new AlertDialog.Builder(poleCtrl.this);
                position_Edit.setTitle("请输入目标位置");
                final EditText edit_position = new EditText(poleCtrl.this);
                position_Edit.setView(edit_position);

                position_Edit.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String charge = edit_position.getText().toString();
                        String go_to;
                        if (null == charge) {
                            Toast.makeText(poleCtrl.this, "位置不能为空，请正确输入位置信息", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            go_to = "G " + charge + " |";
                            sendMes(go_to);
                        }


                    }
                });
                position_Edit.setNegativeButton("取消", null);
                position_Edit.create();
                position_Edit.show();

            }

        });



    }


    private void sendMes(String order) {
        try {
            // 4、发送信息
            byte[] sendData = order.getBytes(Charset.forName("UTF-8"));
            outputStream.write(sendData, 0, sendData.length);
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String intToIp(int paramInt) //得到路由IP
    {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }



    public void setUI(){
        switch (ClickButton){
            case(0):
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.stop);
                break;
            case(1):
                BT_forword.setBackgroundResource(R.drawable.gup);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(2):
                BT_backword.setBackgroundResource(R.drawable.gdown);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(3):
                BT_left.setBackgroundResource(R.drawable.gleft);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(4):
                BT_right.setBackgroundResource(R.drawable.gright);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
        }
    }

}
