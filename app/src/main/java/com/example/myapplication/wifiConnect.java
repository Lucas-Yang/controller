package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;

public class wifiConnect extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    //public int slevel;
    protected WifiAdmin mWifiAdmin;
    protected String ssid;
    private Button open_wifi, close_wifi, scan_wifi;
    private ListView mlistView;
    private List<ScanResult> mWifiList;
    private boolean checkWifi = true;
    //监听wifi状态 广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo()
                        .getSSID();
                Toast.makeText(context, wifiSSID + "连接成功", Toast.LENGTH_SHORT).show();
            }
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);
        mWifiAdmin = new WifiAdmin(wifiConnect.this);
        initViews();

        //////////////
        //自动连接指定小车所载wifi模块AP
        /*
        int i =0;
        while(checkWifi) {
            try{
                if(i==0) {
                    Toast.makeText(getApplicationContext(), "正在连接第一辆小车", Toast.LENGTH_SHORT).show();
                    mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("1-car", "12345678", 3));
                }
                if(i==1) {
                    Toast.makeText(getApplicationContext(), "第一辆车连接失败，正在连接第二辆小车", Toast.LENGTH_SHORT).show();
                    mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("2-car", "12345678", 3));
                }
                if(i==2){
                    Toast.makeText(getApplicationContext(), "第一第二辆车连接失败，正在连接第二辆小车", Toast.LENGTH_SHORT).show();
                    mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("3-car", "12345678", 3));
                }
            }catch (Exception e){
                i++;
                if(i<3) continue;
                else Toast.makeText(getApplicationContext(), "小车全部阵亡", Toast.LENGTH_SHORT).show();
            }
            checkWifi = false;
        }
        */

        //////////

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ////////////////
                /*
                //连接指定wifi AP
                ssid = mWifiList.get(position).SSID;
                if(ssid == "1-car")mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid,"12345678", 3));
                if(ssid == "2-car")mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid,"22222222", 3));
                if(ssid == "3-car")mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid,"33333333", 3));

                ////////////////////
                */

                int type;
                type = mWifiAdmin.checkWifiPassword(wifiConnect.this);
                switch (type) {
                    case 0:
                        AlertDialog.Builder alert = new AlertDialog.Builder(wifiConnect.this);
                        ssid = mWifiList.get(position).SSID;
                        alert.setTitle(ssid);
                        alert.setMessage("输入密码");
                        final EditText et_password = new EditText(wifiConnect.this);
                        final SharedPreferences preferences = getSharedPreferences("wifi_password", Context.MODE_PRIVATE);
                        et_password.setText(preferences.getString(ssid, ""));
                        alert.setView(et_password);
                        alert.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pw = et_password.getText().toString();
                                if (null == pw || pw.length() < 8) {
                                    Toast.makeText(wifiConnect.this, "密码至少8位", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(ssid, pw);   //保存密码
                                editor.commit();
                                mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid, et_password.getText().toString(), 3));
                            }
                        });
                        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                //mWifiAdmin.removeWifi(mWifiAdmin.getNetworkId());
                            }
                        });
                        alert.create();
                        alert.show();
                        break;
                    case 1:
                        mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid,"", 1));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_wifi:
                Toast.makeText(getApplicationContext(), "打开wifi", Toast.LENGTH_SHORT).show();
                mWifiAdmin.openWifi();
                int i =0;
                while(checkWifi) {
                    try{
                        if(i==0) {
                            Toast.makeText(getApplicationContext(), "正在连接第一辆小车", Toast.LENGTH_SHORT).show();
                            mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("A", "11235813", 3));
                        }
                        if(i==1) {
                            Toast.makeText(getApplicationContext(), "第一辆车连接失败，正在连接第二辆小车", Toast.LENGTH_SHORT).show();
                            mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("B", "11235813", 3));
                        }
                        if(i==2){
                            Toast.makeText(getApplicationContext(), "第一第二辆车连接失败，正在连接第二辆小车", Toast.LENGTH_SHORT).show();
                            mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo("C", "11235813", 3));
                        }
                    }catch (Exception e){
                        i++;
                        if(i<3) continue;
                        else Toast.makeText(getApplicationContext(), "小车全部阵亡", Toast.LENGTH_SHORT).show();
                    }
                    checkWifi = false;
                }
                break;
            case R.id.close_wifi:
                Toast.makeText(getApplicationContext(), "关闭wifi", Toast.LENGTH_SHORT).show();
                mWifiAdmin.closeWifi();
                break;
            case R.id.scan_wifi:
                Toast.makeText(getApplicationContext(), "扫描wifi", Toast.LENGTH_SHORT).show();
                mWifiAdmin.startScan(wifiConnect.this);
                mWifiList = mWifiAdmin.getWifiList();
                if (mWifiList != null) {
                    mlistView.setAdapter(new MyAdapter(this, mWifiList));
                }
                break;
            default:
                break;
        }
    }

    /*
    * 控件初始化
    * */
    private void initViews() {
        open_wifi = (Button) findViewById(R.id.open_wifi);
        close_wifi = (Button) findViewById(R.id.close_wifi);
        scan_wifi = (Button) findViewById(R.id.scan_wifi);
        mlistView = (ListView) findViewById(R.id.wifi_list);
        open_wifi.setOnClickListener(wifiConnect.this);
        close_wifi.setOnClickListener(wifiConnect.this);
        scan_wifi.setOnClickListener(wifiConnect.this);
    }



    //重新定义配置器
    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);
            ScanResult scanResult = list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.ssid);
            textView.setText(scanResult.SSID);
            ImageView imageView = (ImageView) view.findViewById(R.id.wifi_level);
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi1));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi2));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi3));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi4));
            }
            else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi4));
            }
            return view;
        }

    }








}
