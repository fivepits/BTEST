package kpog.jp.btest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter = null;

//    ImageView img01;
//    ImageView img02;
    ImageView[] iArray ;

    ArrayList<ImageView> imgArry;

    LinearLayout dataLayout;


    int index=0;

    int fwy;
    int raf;
    int bk1;
    int bk2;
    int grn;

    boolean[] status;

    final int REQUEST_ENABLE_BT = 1; //任意のコード
    UUID MY_UUID = UUID.fromString("00001116-0000-1000-8000-00805f9b34fb"); //IP

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


        status = new boolean[20];

        fwy = R.mipmap.fwy_mdpi;
        raf = R.mipmap.raf_mdpi;
        bk1 = R.mipmap.bk1_mdpi;
        bk2 = R.mipmap.bk2_mdpi;
        grn = R.mipmap.grn_mdpi;

         dataLayout = (LinearLayout) findViewById( R.id.dataView);

//        iArray = new ImageView[10];
    }

    private void changeBtn(View view, int resValue) {
        ImageView img = new ImageView(this);
        img.setImageResource(resValue);
        int w = ViewGroup.LayoutParams.MATCH_PARENT;
        int c = ViewGroup.LayoutParams.WRAP_CONTENT;

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // クリック時の処理
                int i = 0;
                for (ImageView image: imgArry) {
                    if(v.equals(image)) {
                        break;
                    }
                    i++;
                }
                Toast.makeText(getApplicationContext(), "place=" +i, Toast.LENGTH_SHORT).show();
                imgArry.remove(i);
                dataLayout.removeViewAt(i);

            }
        });


        dataLayout.addView(img, new LinearLayout.LayoutParams(c,c));
        imgArry.add(img);
    }

    public void click_Btn1(View view) {
        changeBtn(view, fwy);
    }

    public void click_Btn2(View view) {
        changeBtn(view, raf);
    }

    public void click_Btn3(View view) {
        changeBtn(view, bk1);
    }

    public void click_Btn4(View view) {
        changeBtn(view, bk2);
    }

    public void click_Btn5(View view) {
        changeBtn(view, grn);
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
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();
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

