package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private  ImageButton BT_setCon;
    private  ImageButton BT_poleCtrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BT_setCon = (ImageButton) findViewById(R.id.BT_setBT);
        BT_poleCtrl = (ImageButton) findViewById(R.id.BT_poleCtrl);



        BT_setCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"设置连接", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, wifiConnect.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 0);
            }
        });

        BT_poleCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"按键控制", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, poleCtrl.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent,0);
            }
        });


    }

}
