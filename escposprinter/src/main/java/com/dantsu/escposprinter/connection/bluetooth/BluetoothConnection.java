package com.dantsu.escposprinter.connection.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import java.lang.reflect.Method;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import android.util.Log;

public class BluetoothConnection extends DeviceConnection {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket = null;

    /**
     * Create un instance of BluetoothConnection.
     *
     * @param device an instance of BluetoothDevice
     */
    public BluetoothConnection(BluetoothDevice device) {
        super();
        this.device = device;
    }

    /**
     * Get the instance BluetoothDevice connected.
     *
     * @return an instance of BluetoothDevice
     */
    public BluetoothDevice getDevice() {
        return this.device;
    }

    /**
     * Check if OutputStream is open.
     *
     * @return true if is connected
     */
    @Override
    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && super.isConnected();
    }

    /**
     * Start socket connection with the bluetooth device.
     */
    @SuppressLint("MissingPermission")
    public BluetoothConnection connect() throws EscPosConnectionException {
        Log.d("LOGGRUBPRINTER",  "START CONNECT TO BLUE CONNECTION");
        if (this.isConnected()) {
            Log.d("LOGGRUBPRINTER",  "IS CONNECTED");
            return this;
        }
        Log.d("LOGGRUBPRINTER",  "IS CONNECTED");

        if (this.device == null) {
            throw new EscPosConnectionException("Bluetooth device is not connected.");
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        UUID uuid = this.getDeviceUUID();

        Log.d("LOGGRUBPRINTER", "UUID IS:" );
        Log.d("LOGGRUBPRINTER", uuid.toString());
        try {
            //this.socket = this.device.createRfcommSocketToServiceRecord(uuid);
             this.socket = this.device.createInsecureRfcommSocketToServiceRecord(uuid);
            //Method m = this.device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            // this.socket = (BluetoothSocket) m.invoke(this.device, 1);



            Log.d("LOGGRUBPRINTER","AFTER CREATE SOCKET");
            bluetoothAdapter.cancelDiscovery();
            Log.d("LOGGRUBPRINTER","AFTER cancelDiscovery");
            this.socket.connect();
            Log.d("LOGGRUBPRINTER","AFTER connect");
            this.outputStream = this.socket.getOutputStream();
            Log.d("LOGGRUBPRINTER","AFTER outputStream");
            this.data = new byte[0];
        } catch (IOException e) {
            Log.d("LOGGRUBPRINTER","Unable to connect to bluetooth device.");
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException("Unable to connect to bluetooth device. "+e.getMessage());
        } catch (Exception e) {
            Log.d("LOGGRUBPRINTER","Unable to connect to bluetooth device Exception.");
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException("Unable to connect to bluetooth device."+e.getMessage());
        }
        /*catch (NoSuchMethodException e) {
            Log.d("LOGGRUBPRINTER","Unable to connect to bluetooth device NoSuchMethodException.");
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            Log.d("LOGGRUBPRINTER","Unable to connect to bluetooth device IllegalAccessException.");
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException(e.getMessage());
        }
        catch (InvocationTargetException e) {
            Log.d("LOGGRUBPRINTER","Unable to connect to bluetooth device InvocationTargetException.");
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException(e.getMessage());
        }*/
        Log.d("LOGGRUBPRINTER","end connect method.");
        return this;
    }

    /**
     * Get bluetooth device UUID
     */
    protected UUID getDeviceUUID() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        
        Log.d("LOGGRUBPRINTER","SCANNNNNNN UIIID 1");
        scanner.startScan(new ScanCallback() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) 
            {
                Log.d("LOGGRUBPRINTER","DETECT UIIIDSSSSSSSS");
                Log.d("LOGGRUBPRINTER","DETECT UIIIDSSSSSSSS"+result.toString());
                ParcelUuid[] uuids = result.getScanRecord().getServiceUuids();
                if (uuids != null && uuids.length > 0) {
                    for (int i=0; i<uuids.length; i++) {
                    Log.d("LOGGRUBPRINTER","GET UIIID SCAN"+uuids[i].getUuid().toString());
                    }
                }
            }
        });






        Log.d("LOGGRUBPRINTER","GET UIIID 1");
        ParcelUuid[] uuids = this.device.getUuids();
        if (uuids != null && uuids.length > 0) {
            Log.d("LOGGRUBPRINTER","GET UIIID 2");
            if (false && Arrays.asList(uuids).contains(new ParcelUuid(BluetoothConnection.SPP_UUID))) {
                return BluetoothConnection.SPP_UUID;
            }
            Log.d("LOGGRUBPRINTER","GET UIIID 3 "+uuids[0].getUuid().toString());
            return uuids[0].getUuid();
        } else {
            Log.d("LOGGRUBPRINTER","GET UIIID 4");
            return BluetoothConnection.SPP_UUID;
        }
    }

    /**
     * Close the socket connection with the bluetooth device.
     */
    public BluetoothConnection disconnect() {
        this.data = new byte[0];
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputStream = null;
        }
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.socket = null;
        }
        return this;
    }

}
