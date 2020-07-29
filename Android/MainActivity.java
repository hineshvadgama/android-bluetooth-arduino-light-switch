package com.example.arduinolightswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private int REQUEST_ENABLE_BT = 1;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set to the corresponding switch in AndroidManifest.xml
        final Switch lightSwitch = (Switch) findViewById(R.id.lightSwitch);
        boolean isConnected = initialiseBluetoothConnection();

        if (isConnected == true) {

            try {
                write("n");
            } catch (IOException e) {
                Log.e(TAG, "The method write() failed to send the data");
            }

            lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (lightSwitch.isChecked()) {
                        // System.out.println("The light switch is on");
                        try {
                            write("y");
                        } catch (IOException e) {
                            Log.e(TAG, "The method write() failed to send the data");
                        }
                    } else {
                        // System.out.println("The light switch is off");
                        try {
                            write("n");
                        } catch (IOException e) {
                            Log.e(TAG, "The method write() failed to send the data");
                        }
                    }
                }
            });
        }
    }

    private boolean initialiseBluetoothConnection() {
        boolean socketStatus = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    BluetoothDevice bluetoothModule = null;
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equalsIgnoreCase("HC-05")) {
                            bluetoothModule = device;
                            // System.out.println("We have connected to " + device.getName());
                        }
                    }
                    if (bluetoothModule != null) {
                        ParcelUuid[] uuids = bluetoothModule.getUuids();
                        try {
                            BluetoothSocket socket = bluetoothModule.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            outputStream = socket.getOutputStream();
                            socketStatus = true;
                        } catch (IOException e) {
                            Log.e(TAG, "The socket failed to connect");
                            socketStatus = false;
                        }
                    }
                }
            }
        }
        return socketStatus;
    }

    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

}
