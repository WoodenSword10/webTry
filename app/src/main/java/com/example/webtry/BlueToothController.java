package com.example.webtry;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 蓝牙适配器
 */
public class BlueToothController implements Serializable {
    // 成员变量
    private BluetoothSocket btSocket;
    private BluetoothAdapter mAdapter;
    private String TAG = "";
    public static final int RECV_VIEW = 0;
    public static final int NOTICE_VIEW = 1;

    /**
     * 构造函数
     */
    public BlueToothController(){
        // 获取本地的蓝牙适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 是否支持蓝牙
     * @return true支持，false不支持
     */
    public boolean isSupportBlueTooth(){
        // 若支持蓝牙，则本地适配器不为null
        if(mAdapter != null){
            return true;
        }
        // 否则不支持
        else{
            return false;
        }
    }

    /**
     * 判断当前蓝牙状态
     * @return true为打开，false为关闭
     */
    public boolean getBlueToothStatus(){
        // 断言？为了避免mAdapter为null导致return出错
        assert (mAdapter != null);
        // 蓝牙状态
        return mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth(Activity activity, int requestCode){
        if(!mAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public void turnOffBlueTooth() {
        if(mAdapter.isEnabled()) {
            mAdapter.disable();
        }
    }

    /**
     * 打开蓝牙可见性
     * @param context
     */
    public void enableVisibly(Context context){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

    /**
     * 查找设备
     */
    public boolean findDevice(){
        assert(mAdapter!=null);
        if(mAdapter.isDiscovering()){
            mAdapter.cancelDiscovery();
            return false;
        }else {
            return mAdapter.startDiscovery();
        }
    }

    /**
     * 获取绑定设备
     * @return
     */
    public ArrayList<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<BluetoothDevice>(mAdapter.getBondedDevices());
    }

    /**
     * 发现设备
     * @param addr
     * @return
     */
    public BluetoothDevice find_device(String addr){
        return mAdapter.getRemoteDevice(addr);
    }

    /**
     * 连接设备
     */
    public void connect_init(BluetoothDevice device){
        mAdapter.cancelDiscovery();
        try{
            Method clientMethod = device.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[]{int.class});
            btSocket = (BluetoothSocket)clientMethod.invoke(device, 1);
            connect(btSocket);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connect(final BluetoothSocket btSocket){
        try {
            if (btSocket.isConnected()){
                Log.e(TAG, "connect: 已经连接");
                return;
            }
            btSocket.connect();
            if (btSocket.isConnected()){
                Log.e(TAG, "connect: 连接成功");
            }else{
                Log.e(TAG, "connect: 连接失败");
                btSocket.close();

            }
        }catch (Exception e){e.printStackTrace();}
    }

    public void cancelSearch() {
        mAdapter.cancelDiscovery();
    }
}
