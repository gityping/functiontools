package com.yping.functiontools.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yping.functiontools.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * 1.开启蓝牙/2.扫描蓝牙/3.配对蓝牙/4.连接蓝牙/5.通信
 * */
public class BluetoothDevelopmentActivity extends Activity {
    private BluetoothAdapter mBluetoothAdapter;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_development);
        initView();
    }

    private void initView() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(scanBlueReceiver, filter1);
        registerReceiver(scanBlueReceiver, filter2);
        registerReceiver(scanBlueReceiver, filter3);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pinBlueReceiver, filter4);
        registerReceiver(pinBlueReceiver, filter5);

    }

    /**
     * 扫描广播接收类
     * Created by zqf on 2018/7/6.
     */

    public class ScanBlueReceiver extends BroadcastReceiver {
        private final String TAG = ScanBlueReceiver.class.getName();
        private ScanBlueCallBack callBack;

        public ScanBlueReceiver(ScanBlueCallBack callBack) {
            this.callBack = callBack;
        }

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "开始扫描...");
                    callBack.onScanStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "结束扫描...");
                    callBack.onScanFinished();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "发现设备...");
                    callBack.onScanning(device);
                    break;
            }
        }
    }

    /**
     * 配对广播接收类
     * Created by zqf on 2018/7/7.
     */

    public class PinBlueReceiver extends BroadcastReceiver {
        private String pin = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000
        private final String TAG = PinBlueReceiver.class.getName();
        private PinBlueCallBack callBack;

        public PinBlueReceiver(PinBlueCallBack callBack) {
            this.callBack = callBack;
        }

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
                    callBack.onBondRequest();
                    //1.确认配对
//                ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                    Method setPairingConfirmation = device.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
                    setPairingConfirmation.invoke(device, true);
                    //2.终止有序广播
                    Log.d("order...", "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
//                boolean ret = ClsUtils.setPin(device.getClass(), device, pin);
                    Method removeBondMethod = device.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
                    Boolean returnValue = (Boolean) removeBondMethod.invoke(device, new Object[]{pin.getBytes()});
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "取消配对");
                        callBack.onBondFail(device);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "配对中");
                        callBack.onBonding(device);
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "配对成功");
                        callBack.onBondSuccess(device);
                        break;
                }
            }
        }
    }

    /**
     * 设备是否支持蓝牙  true为支持
     *
     * @return
     */
    public boolean isSupportBlue() {
        return mBluetoothAdapter != null;
    }


    /**
     * 蓝牙是否打开   true为打开
     *
     * @return
     */
    public boolean isBlueEnable() {
        return isSupportBlue() && mBluetoothAdapter.isEnabled();
    }

    /**
     * 自动打开蓝牙（异步：蓝牙不会立刻就处于开启状态）
     * 这个方法打开蓝牙不会弹出提示
     */
    public void openBlueAsyn() {
        if (isSupportBlue()) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * 自动打开蓝牙（同步）
     * 这个方法打开蓝牙会弹出提示
     * 需要在onActivityResult 方法中判断resultCode == RESULT_OK  true为成功
     */
    public void openBlueSync(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 开启GPS
     *
     * @param permission
     */
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("当前手机扫描蓝牙需要打开定位功能。")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("前往设置",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    //GPS已经开启了
                }
                break;
        }
    }

    /**
     * 检查GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /**
     * 扫描的方法 返回true 扫描成功
     * 通过接收广播获取扫描到的设备
     *
     * @return
     */
    public boolean scanBlue() {
        if (!isBlueEnable()) {
            Log.e("yping", "Bluetooth not enable!");
            return false;
        }

        //当前是否在扫描，如果是就取消当前的扫描，重新扫描
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        //此方法是个异步操作，一般搜索12秒
        return mBluetoothAdapter.startDiscovery();
    }

    /**
     * 取消扫描蓝牙
     *
     * @return true 为取消成功
     */
    public boolean cancelScanBule() {
        if (isSupportBlue()) {
            return mBluetoothAdapter.cancelDiscovery();
        }
        return true;
    }

    /**
     * 配对（配对成功与失败通过广播返回）
     *
     * @param device
     */
    public void pin(BluetoothDevice device) {
        if (device == null) {
            Log.e("yping", "bond device null");
            return;
        }
        if (!isBlueEnable()) {
            Log.e("yping", "Bluetooth not enable!");
            return;
        }
        //配对之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Log.d("yping", "attemp to bond:" + device.getName());
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("yping", "attemp to bond fail!");
            }
        }
    }

    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     *
     * @param device
     */
    public void cancelPinBule(BluetoothDevice device) {
        if (device == null) {
            Log.d("yping", "cancel bond device null");
            return;
        }
        if (!isBlueEnable()) {
            Log.e("yping", "Bluetooth not enable!");
            return;
        }
        //判断设备是否配对，没有配对就不用取消了
        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            Log.d("yping", "attemp to cancel bond:" + device.getName());
            try {
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("yping", "attemp to cancel bond fail!");
            }
        }
    }


    /**
     * 连接线程
     * Created by zqf on 2018/7/7.
     */

    public class ConnectBlueTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {
        private final String TAG = ConnectBlueTask.class.getName();
        private BluetoothDevice bluetoothDevice;
        private ConnectBlueCallBack callBack;

        public ConnectBlueTask(ConnectBlueCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            bluetoothDevice = bluetoothDevices[0];
            BluetoothSocket socket = null;
            try {
                Log.d(TAG, "开始连接socket,uuid:" + ClassicsBluetooth.UUID);
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(ClassicsBluetooth.UUID));
                if (socket != null && !socket.isConnected()) {
                    socket.connect();
                }
            } catch (IOException e) {
                Log.e(TAG, "socket连接失败");
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, "socket关闭失败");
                }
            }
            return socket;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "开始连接");
            if (callBack != null) callBack.onStartConnect();
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                Log.d(TAG, "连接成功");
                if (callBack != null) callBack.onConnectSuccess(bluetoothDevice, bluetoothSocket);
            } else {
                Log.d(TAG, "连接失败");
                if (callBack != null) callBack.onConnectFail(bluetoothDevice, "连接失败");
            }
        }
    }

    /**
     * 连接 （在配对之后调用）
     *
     * @param device
     */
    public void connect(BluetoothDevice device, ConnectBlueCallBack callBack) {
        if (device == null) {
            Log.d(TAG, "bond device null");
            return;
        }
        if (!isBlueEnable()) {
            Log.e(TAG, "Bluetooth not enable!");
            return;
        }
        //连接之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        new ConnectBlueTask(callBack).execute(device);
    }

    /**
     * 蓝牙是否连接
     *
     * @return
     */
    public boolean isConnectBlue() {
        return mBluetoothSocket != null && mBluetoothSocket.isConnected();
    }

    /**
     * 断开连接
     *
     * @return
     */
    public boolean cancelConnect() {
        if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        mBluetoothSocket = null;
        return true;
    }

    /**
     * 输入mac地址进行自动配对
     * 前提是系统保存了该地址的对象
     *
     * @param address
     * @param callBack
     */
    public void connectMAC(String address, ConnectBlueCallBack callBack) {
        if (!isBlueEnable()) {
            return;
        }
        BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(address);
        connect(btDev, callBack);
    }


    /**
     * 读取线程
     * Created by zqf on 2018/7/7.
     */

    public class ReadTask extends AsyncTask<String, Integer, String> {
        private final String TAG = ReadTask.class.getName();
        private ReadCallBack callBack;
        private BluetoothSocket socket;

        public ReadTask(ReadCallBack callBack, BluetoothSocket socket) {
            this.callBack = callBack;
            this.socket = socket;
        }

        @Override
        protected String doInBackground(String... strings) {
            BufferedInputStream in = null;
            try {
                StringBuffer sb = new StringBuffer();
                in = new BufferedInputStream(socket.getInputStream());

                int length = 0;
                byte[] buf = new byte[1024];
                while ((length = in.read()) != -1) {
                    sb.append(new String(buf, 0, length));
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "读取失败";
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "开始读取数据");
            if (callBack != null) callBack.onStarted();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "完成读取数据");
            if (callBack != null) {
                if ("读取失败".equals(s)) {
                    callBack.onFinished(false, s);
                } else {
                    callBack.onFinished(true, s);
                }
            }
        }
    }

    /**
     * 写入线程
     * Created by zqf on 2018/7/7.
     */

    public class WriteTask extends AsyncTask<String, Integer, String> {
        private  final String TAG = WriteTask.class.getName();
        private WriteCallBack callBack;
        private BluetoothSocket socket;

        public WriteTask(WriteCallBack callBack, BluetoothSocket socket) {
            this.callBack = callBack;
            this.socket = socket;
        }

        @Override
        protected String doInBackground(String... strings) {
            String string = strings[0];
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();

                outputStream.write(string.getBytes());
            } catch (IOException e) {
                Log.e("error", "ON RESUME: Exception during write.", e);
                return "发送失败";
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "发送成功";


        }

        @Override
        protected void onPreExecute() {
            if (callBack != null) callBack.onStarted();
        }

        @Override
        protected void onPostExecute(String s) {
            if (callBack != null) {
                if ("发送成功".equals(s)) {
                    callBack.onFinished(true, s);
                } else {
                    callBack.onFinished(false, s);
                }

            }
        }
    }
}
