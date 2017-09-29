package kpog.jp.btest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter = null;

    final int REQUEST_ENABLE_BT = 1; //任意のコード
    UUID MY_UUID = UUID.fromString("00001112-0000-1000-8000-00805f9b34fb"); //IP

    ArrayAdapter<String> mArrayAdapter;

    TextView tv01;

    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bluetoothアダプタ
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        tv01 = (TextView)findViewById(R.id.textView);

        //mArrayAdapter = new ArrayAdapter<String>(this, R.id.textView, null);

        BluetoothDevice device = checkBT();

        if(device != null){
            connectBT(device);
            //comfirmBT();
        }


    }


    @Nullable
    private BluetoothDevice checkBT() {

        //Bluetoothアダプタが存在するか
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            setMessage("Bluetoothアダプタがありません");
            return null;
        }

        //BluetoothがONかどうか
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            setMessage("BuletoothをONにしました。");
        }

        //ペア設定済みの端末の問い合わせ
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                setMessage(device.getName() + " - MAC Address=" + device.getAddress());
                for(int i=0; i<device.getUuids().length; i++) {
                        UUID tmpUuid = device.getUuids()[i].getUuid();
                    if(tmpUuid.equals(MY_UUID)) {
                        setMessage(MY_UUID.toString());
                        setMessage(String.valueOf(device.getType()));
                        return device;
                    } else {
                        setMessage(tmpUuid.toString());

                    }
                }
            }

        }
        return null;
    }


    private void connectBT(BluetoothDevice device) {

        ConnectThread th = new ConnectThread(device);
        th.start();

    }

    private void comfirmBT() {
        while(true) {
            if (mmSocket != null && mmSocket.isConnected()) {
                setMessage(mmSocket.getRemoteDevice().getName() + "に接続してます。");
                break;
            } else {
                setMessage(mmSocket.getRemoteDevice().getName() + "に接続してません。");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMessage(String data) {
        tv01.setText(tv01.getText() + "\n" + data);
    }


    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();
                // MY_UUID is the app's UUID string, also used by the server code
                setMessage(MY_UUID.toString());
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                setMessage("接続準備中....." );
            } catch (IOException e) {
                setMessage(e.toString());
            }

            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                if (mmSocket != null && mmSocket.isConnected()) {
                    setMessage(mmSocket.getRemoteDevice().getName() + "に接続してます。");
                } else {
                    setMessage(mmSocket.getRemoteDevice().getName() + "に接続してません。");
                }
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                setMessage("接続するよー....." );
                mmSocket.connect();
//                setMessage(mmSocket.getRemoteDevice().getName() + "に接続しました。");
            } catch (IOException e) {
                setMessage(e.toString());
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            } catch (Exception e) {
                setMessage(e.toString());
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}

