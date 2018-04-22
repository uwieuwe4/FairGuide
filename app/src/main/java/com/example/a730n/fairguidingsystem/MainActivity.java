package com.example.a730n.fairguidingsystem;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "MainActivity";




    BluetoothAdapter mBluetoothAdapter;
    // Button btnEnableDisable_Discoverable;
    Button btnTxOK;
    EditText etTxPower;
    BluetoothConnectionService mBluetoothConnection;
    Button btnKarte;
    Hashtable<String, Double> distances;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>(); //In this list the beacons are saved
    public ArrayList<String> mBTDevicesString = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;



//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BottomNavigationView navigation = findViewById(R.id.navigation);
        btnTxOK = findViewById(R.id.btnOKTX);
        etTxPower = findViewById(R.id.etTxPower);
        btnKarte = findViewById(R.id.btnKarte);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Button btnONOFF = findViewById(R.id.btnONOFF);
        lvNewDevices = findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        //SHOW MESSAGEBOX for INFORMATION ABOUT ACTIVITY
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("This page is only here for demonstration and Testing Purposes. When clicking 'Discover' you get a list of all the BT-Devices available");
        dlgAlert.setTitle("Welcome");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();


        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);


        btnKarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);
            }
        });

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                //enableDisableBT();
                Toast.makeText(getApplicationContext(), "Nicht implementiert.", Toast.LENGTH_LONG).show();
            }
        });

        btnTxOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Noch nicht implementiert.. :/ ",  Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
  //  private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
//
//                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
//
//                switch (mode) {
//                    //Device is in Discoverable Mode
//                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
//                        break;
//                    //Device not in discoverable mode
//                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
//                        break;
//                    case BluetoothAdapter.SCAN_MODE_NONE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
//                        break;
//                    case BluetoothAdapter.STATE_CONNECTING:
//                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
//                        break;
//                    case BluetoothAdapter.STATE_CONNECTED:
//                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
//                        break;
//                }
//
//}
////own approach
//            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    short  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//                    Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
//                    }
//                    }
//                    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);mBTDevices.add(device);//own approach
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    int txPower = -58;
                    double distance  = calculateAccuracy(txPower, rssi);
                    DecimalFormat df = new DecimalFormat("#.##");
                    //Alte berechnung
//                    double distance;
//                    //if(rssi == 0) return -1.0;
//                    double ratio = rssi * 1.0 / txPower;
//                    if (ratio < 1) {
//                        distance = Math.pow(ratio, 10);
//                    } else {
//                        distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;


                       // if(device.getAddress().startsWith("C1") || device.getAddress().startsWith("C2"))
                        Toast.makeText(getApplicationContext(), "  Abstand: " + df.format(distance) + "M  zu " +device.getAddress(), Toast.LENGTH_SHORT).show();
                        //when we dont have 3 beacons yet
                        if(distances.size() < 3)
                        {
                            distances.put(device.getAddress(), distance);
                        }
                        else //todo: delete else
                        {
                            Toast.makeText(getApplicationContext(), createJson(distances), Toast.LENGTH_LONG).show();
                            distances.clear();
                        }

                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                        mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                        lvNewDevices.setAdapter(mDeviceListAdapter);
                    }
                //}
//            }
        }
    };

    //berechnet die Enffernung zum Beacon/BT-Gerät
    private static double calculateAccuracy(int txPower, short rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    //creates JSON file needed for the triangulate position-function
    private String createJson(Hashtable<String, Double> distances)
    {
        String json = "{ 'nickname' = 'tom45', 'timestamp' = '2018/04/20 16:20:00', 'beacons':  [" ;
        String mac;
        Set<String> keys = distances.keySet();
        Iterator<String> itr = keys.iterator();
        if(distances.size() != 3) return null;
        while(itr.hasNext()) {
            mac = itr.next();
            //System.out.println("Key: "+mac+" & Value: "+ distances.get(mac));
            json += "{ 'beaconId': " +distances.get(mac) + ", 'distance': " + mac +"}, ";
        }

        return json;
    }

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    private double[] triangulatePosition(String json) {
        double[] position = new double[2]; //array for returning the position
        //This is kind of messy but it makes the maths below easier to read
        double dA = 5; //distance to beacon 1 (TODO: get those from JSON)
        double dB = 7; //distance from beacon 2
        double dC = 9; //distance from beacon 3
        double ax = 50; //x-coordinate of beacon 1 //TODO: Get this from DB according to beaconID
        double ay = 90; //y-coordinate of beacon 1
        double bx = 30;
        double by = 70;
        double cx = 160; //x-coordinate of beacon 3
        double cy = 190; // y-coordinate of beacon 4

        //Maths taken from https://stackoverflow.com/a/20976803
        double W = dA*dA - dB*dB - ax*ax - ay*ay + bx*bx + by*by;
        double Z = dB*dB - dC*dC - bx*bx - by*by + cx*cx + cy*cy;
        double x = (W*(cy-by) - Z*(by-ay)) / (2 * ((bx-ax)*(cy-by) - (cx-bx)*(by-ay)));
        double y;

        //If two of the three beacons share an identical coordinate then the certain operations can break (divide by 0), this code helps avoid that.
        if (by-ay != 0){
            y = (W - 2*x*(bx-ax)) / (2*(by-ay));
            if (cy-by != 0){
                //This helps to make the result more accurate and reduce the margin of error.
                double y2 = (Z - 2*x*(cx-bx)) / (2*(cy-by));
                y = (y + y2) / 2;
            }
        }else{
            y = (Z - 2*x*(cx-bx)) / (2*(cy-by));
        }

        position[0] = x;
        position[1] = y;


        return position;
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
       // unregisterReceiver(mBroadcastReceiver1);
       // unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }



    //calculate Position{

   // }
//
//    public void startConnection(){
//        startBTConnection(mBTDevice, MY_UUID_INSECURE);
//    }
//
//    public void startBTConnection(BluetoothDevice device, UUID uuid){
//        Log.d(TAG, "startBTConnection: Bluetooth verbindung instantialisieren");
//        mBluetoothConnection.startClient(device, uuid);
//
//    }

//    public void enableDisableBT(){
//        if(mBluetoothAdapter == null){
//            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
//        }
//        if(!mBluetoothAdapter.isEnabled()){
//            Log.d(TAG, "enableDisableBT: enabling BT.");
//            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBTIntent);
//
//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            registerReceiver(mBroadcastReceiver1, BTIntent);
//        }
//        if(mBluetoothAdapter.isEnabled()){
//            Log.d(TAG, "enableDisableBT: disabling BT.");
//            mBluetoothAdapter.disable();
//
//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            registerReceiver(mBroadcastReceiver1, BTIntent);
//        }
//
//    }

//TODO: Can this be deleted? //Changes discoverability
//    public void btnEnableDisable_Discoverable(View view) {
//        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
//
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);
//
//        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//        registerReceiver(mBroadcastReceiver2,intentFilter);
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }
}
