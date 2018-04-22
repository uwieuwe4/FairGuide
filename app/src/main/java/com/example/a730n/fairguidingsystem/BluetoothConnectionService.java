package com.example.a730n.fairguidingsystem;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by 730N on 03.03.2018.
 */

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "FairGuidingSystem";

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private ConnectedThread mConnectedThread;

    Context mContext;
    private AcceptThread mInsecureAcceptThread;


    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }
    //Dieser Thread wartet auf eingehende Verbindungen
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread()
        {
            BluetoothServerSocket tmp = null;
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }
            catch (Exception e)
            {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }
            mmServerSocket = tmp;
        }
        public void run(){
            Log.d(TAG, "Run: AcceptThread running");
            BluetoothSocket socket = null;
            try{
                Log.d(TAG, "run: RFCOM server socket start...");
                socket = mmServerSocket.accept();
                Log.d(TAG, "run: RFCOM Server SOcket accepted Connection.");
            }
            catch (Exception e)
            {
                Log.e(TAG, "run: IOException: " + e.getMessage() );
            }

            if(socket != null)
            {
                connected(socket, mmDevice);
            }
            Log.i(TAG, "END AcceptThread");
        }

        public void cancel(){
            Log.d(TAG, "cancel: Cancelling AcceptThread");
            try{
                mmServerSocket.close();
            }catch (Exception e){
                Log.e(TAG, "cancel: Close of AcceptThread SOcket failed: " +e.getMessage());
            }
        }
    }



    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread");
            //GEt a BT Socket for a Connection with the given device...
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
                Log.d(TAG, "ConnectThread: Trying to create InsecureRFcommSocket using UUID: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "run: Could not create insecureRFcommSocket: " + e.getMessage());
            }
            mmSocket = tmp;
            //Always cancel discovery  (slows down connection)
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected");
            } catch (IOException e) {
                try{
                    mmSocket.close  ();
                    Log.d(TAG, "run: Closed SOcket");
                } catch (IOException e1) {
                    Log.e(TAG, "run: Unable to close connection in socket" + e.getMessage() );
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " +  MY_UUID_INSECURE);
            }
            connected(mmSocket, mmDevice);
        }
        public void cancel(){
            try{
                Log.d(TAG, "cancel: Closing client socket");
                mmSocket.close();
            }catch (Exception e)
            {
                Log.e(TAG, "cancel: close() of mmsocketin ConnectThread failed" + e.getMessage() );
            }
        }
    }

    public synchronized void start()
    {
        Log.d(TAG, "start");
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mInsecureAcceptThread != null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started");
        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectThread: Starting");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            mProgressDialog.dismiss();
            try{
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }catch (Exception e){
                Log.e(TAG, "ConnectThread: Failed to get InputStream: " + e.getMessage() );
            }
            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024]; //buffer store for the stream
            int bytes; //bytes returned from read()
            while(true){
                try{
                    bytes = mmInputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputStream" + text);
            try {
                mmOutputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to outputStream. " + e.getMessage() );
            }
        }
        public void cancel() {
            try{
                mmSocket.close();
            }catch (Exception e){}
        }
    }
    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }
    public void write(byte[] out){
        ConnectedThread r;
        Log.d(TAG, "write: Write Called");
        mConnectedThread.write(out);
    }
}
