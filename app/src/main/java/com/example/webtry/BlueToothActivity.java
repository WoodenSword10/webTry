package com.example.webtry;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class BlueToothActivity extends AppCompatActivity {
    public BlueToothController btController;
    private Toast mToast;
    //定义一个列表，存蓝牙设备的地址。
    public ArrayList<String> arrayList=new ArrayList<>();
    //定义一个列表，存蓝牙设备地址，用于显示。
    public ArrayList<String> deviceName=new ArrayList<>();
    private ArrayAdapter adapter1;
    private IntentFilter foundFilter;
    public String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        btController = new BlueToothController();
        ListView listView = (ListView) findViewById(R.id.listView1);
        adapter1 = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, deviceName);
        listView.setAdapter(adapter1);
        foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//发现蓝牙设备后的广播
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btController.findDevice();
        registerReceiver(bluetoothReceiver, foundFilter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarxx);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btController.cancelSearch();
                Intent intent = getIntent();
                unregisterReceiver(bluetoothReceiver);
                BlueToothActivity.this.finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence content = ((TextView) view).getText();
                String con = content.toString();
                String[] conArray = con.split("\n");
                String rightStr = conArray[1].substring(5, conArray[1].length());
                BluetoothDevice device = btController.find_device(rightStr);
                if (device.getBondState() == 10) {
                    btController.cancelSearch();
                    String s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未配对" + "\n";
                    deviceName.remove(s);
                    device.createBond();
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对" + "\n";
                    deviceName.add(s);
                    adapter1.notifyDataSetChanged();
                } else {
                    btController.cancelSearch();
                    String s2 = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对" + "\n";
                    if (deviceName.contains(s2)) {
                        if (device.getName().equals("HC-06")){
                            Intent intent = getIntent();
                            Bundle bundle = intent.getExtras();
                            bundle.putString("deviceAddr", device.getAddress());
//                            showToast(device.getAddress());
                            intent.putExtras(bundle);
                            unregisterReceiver(bluetoothReceiver);
                            setResult(1,intent);
                            finish();
                        }
                        else{
                            showToast("不是目标蓝牙，请勿使用");
                        }
                    }
                }
            }
        });
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                String s;
//                Log.e(TAG, "onReceive: 发现新设备");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == 12) {
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对" + "\n";
                }
                else if (device.getBondState() == 10){
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未配对" +"\n";
                }else{
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未知" + "\n";
                }
                if (!deviceName.contains(s)) {
                    deviceName.add(s);//将搜索到的蓝牙名称和地址添加到列表。
                    arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
                    adapter1.notifyDataSetChanged();//更新
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
//                Log.e(TAG, "onReceive: 搜索结束");
                showToast("搜索结束");
                unregisterReceiver(this);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
//                Log.e(TAG, "onReceive: 搜索开始");
                showToast("搜索开始");
            }
        }
    };


    /**
     * Toast弹窗显示
     * @param text  显示文本
     */
    public void showToast(String text){
        if( mToast == null){
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else{
            mToast.setText(text);
        }
        mToast.show();
    }
}