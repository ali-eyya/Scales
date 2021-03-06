package aicare.net.cn.sdk.pabulumsdkrepositoryandroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.base.BaseActivity;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.AppUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.Config;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.SPUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.T;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.view.SetRssiDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.net.aicare.pabulumlibrary.PabulumSDK;
import cn.net.aicare.pabulumlibrary.bleprofile.BleProfileService;
import cn.net.aicare.pabulumlibrary.entity.FoodData;
import cn.net.aicare.pabulumlibrary.pabulum.PabulumService;
import cn.net.aicare.pabulumlibrary.utils.L;
import cn.net.aicare.pabulumlibrary.utils.PabulumBleConfig;
import cn.net.aicare.pabulumlibrary.utils.ParseData;

public class MainActivity extends BaseActivity implements SetRssiDialog.OnQueryListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.ib_title_left)
    ImageButton ibTitleLeft;
    @BindView(R.id.tv_title_middle)
    TextView tvTitleMiddle;
    @BindView(R.id.btn_title_right)
    Button btnTitleRight;
    @BindView(R.id.tv_show_state)
    TextView tvShowState;
    @BindView(R.id.tv_show_rssi)
    TextView tvShowRssi;
    @BindView(R.id.tv_show_version)
    TextView tvShowVersion;
    @BindView(R.id.rg_unit)
    RadioGroup rgUnit;
    @BindView(R.id.rg_unit_two)
    RadioGroup rgUnitTwo;
    @BindView(R.id.et_set_weight)
    EditText etSetWeight;
    @BindView(R.id.tv_show_result)
    TextView tvShowResult;
    @BindView(R.id.tv_show_did)
    TextView tvShowDid;
    @BindView(R.id.tv_show_time)
    TextView tv_show_time;

    @OnClick({R.id.btn_title_right, R.id.tv_show_state, R.id.btn_set_weight, R.id.btn_tare, R.id.btn_power_off, R.id.btn_cal, R.id.btn_all_cal, R.id.btn_fat, R.id.btn_all_fat, R.id.btn_pro,
            R.id.btn_all_pro, R.id.btn_car, R.id.btn_all_car, R.id.btn_fib, R.id.btn_all_fib, R.id.btn_cho, R.id.btn_all_cho, R.id.btn_sod, R.id.btn_all_sod, R.id.btn_sug, R.id.btn_all_sug,
            R.id.btn_write_value, R.id.btn_did, R.id.btn_get_version, R.id.btn_start, R.id.btn_start_less, R.id.btn_pause, R.id.btn_reset, R.id.btn_pause_less, R.id.btn_get_units})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_right:
                new SetRssiDialog(this, defaultRssi, this).show();
                break;
            case R.id.tv_show_state:
                if (binder != null) {
                    binder.disconnect();
                }
                break;
            case R.id.btn_set_weight:
                String weight = etSetWeight.getText().toString().trim();
                if (TextUtils.isEmpty(weight)) {
                    T.showShort(this, R.string.pls_input_weight);
                } else {
                    int wei = Integer.parseInt(weight);
                    if (binder != null) {
                        binder.setWeight(wei);
                    }
                }
                break;
            case R.id.btn_tare:
                if (binder != null) {
                    binder.netWeight();
                }
                break;
            case R.id.btn_power_off:
                if (binder != null) {
                    binder.powerOff();
                    handler.postDelayed(disconnectRunnable, 1000);
                }
                break;
            //??????DID  2018-12-3
            case R.id.btn_did:
                if (binder != null) {
                    L.i(TAG, "??????????????????did");
                    binder.getDid();
                }
                break;
            //2019/4/29
            case R.id.btn_start:
                if (binder != null) {
                    L.i(TAG, "????????????");
                    binder.startTime();
                }
                break;    //2019/5/22
            case R.id.btn_start_less:
                if (binder != null) {
                    L.i(TAG, "???????????????");
                    binder.startTimeLess(180);
                }
                break;
            //2019/6/25
            case R.id.btn_pause:
                if (binder != null) {
                    L.i(TAG, "???????????????");
                    binder.pauseTime(80);
                }
                break;
            //2019/6/25
            case R.id.btn_pause_less:
                if (binder != null) {
                    L.i(TAG, "???????????????");
                    binder.pauseTimeLess(90);
                }
                break;
            //2019/4/29
            case R.id.btn_reset:
                if (binder != null) {
                    L.i(TAG, "????????????");
                    binder.resetTime();
                }
                break;
            case R.id.btn_write_value:
                if (binder != null) {
                    //??????????????????
                    byte[] value = initRandomByteArr(new Random().nextInt(21));
                    L.e(TAG, "value: " + ParseData.arr2Str(value));
                    binder.writeValue(value);
                }
                break;
            case R.id.btn_get_version:
                if (binder != null) {
                    L.i(TAG, "???????????????????????????");
                    binder.getVersion();

                }
                break;
            case R.id.btn_get_units:
                if (binder != null) {
                    L.i(TAG, "??????????????????????????????");
                    binder.getUnits();

                }
                break;
            default:
                setData(view.getId());
                break;
        }
    }

    private byte[] initRandomByteArr(int count) {
        byte[] bytes = new byte[count];
        Random random = new Random();
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = Integer.valueOf(random.nextInt(256)).byteValue();
        }

        return bytes;
    }

    private void setData(int id) {
        String data = etSetWeight.getText().toString().trim();
        if (TextUtils.isEmpty(data)) {
            T.showShort(this, R.string.pls_input_weight);
        } else {
            int wei = Integer.parseInt(data);
            if (binder != null) {
                switch (id) {
                    case R.id.btn_cal:
                        binder.setCal(wei);
                        break;
                    case R.id.btn_all_cal:
                        binder.setAllCal(wei);
                        break;
                    case R.id.btn_fat:
                        binder.setFat(wei);
                        break;
                    case R.id.btn_all_fat:
                        binder.setAllFat(wei);
                        break;
                    case R.id.btn_pro:
                        binder.setPro(wei);
                        break;
                    case R.id.btn_all_pro:
                        binder.setAllPro(wei);
                        break;
                    case R.id.btn_car:
                        binder.setCar(wei);
                        break;
                    case R.id.btn_all_car:
                        binder.setAllCar(wei);
                        break;
                    case R.id.btn_fib:
                        binder.setFib(wei);
                        break;
                    case R.id.btn_all_fib:
                        binder.setAllFib(wei);
                        break;
                    case R.id.btn_cho:
                        binder.setCho(wei);
                        break;
                    case R.id.btn_all_cho:
                        binder.setAllCho(wei);
                        break;
                    case R.id.btn_sod:
                        binder.setSod(wei);
                        break;
                    case R.id.btn_all_sod:
                        binder.setAllSod(wei);
                        break;
                    case R.id.btn_sug:
                        binder.setSug(wei);
                        break;
                    case R.id.btn_all_sug:
                        binder.setAllSug(wei);
                        break;
                }
            }
        }
    }

    @OnCheckedChanged({R.id.rb_g, R.id.rb_lb, R.id.rb_ml, R.id.rb_oz, R.id.rb_kg, R.id.rb_fg, R.id.rb_ml_milk, R.id.rb_ml_water, R.id.rb_floz_milk, R.id.rb_floz_water, R.id.rb_lb_lb})
    void onCheckedChange(RadioButton radioButton, boolean isChecked) {
        if (isBleChangeUnit) {
            isBleChangeUnit = false;
            return;
        }
        if (isChecked) {
            switch (radioButton.getId()) {
                case R.id.rb_g:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_G);
                    }
                    break;
                case R.id.rb_ml:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_ML);
                    }
                    break;
                case R.id.rb_lb:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_LB);
                    }
                    break;
                case R.id.rb_oz:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_OZ);
                    }
                    break;
                case R.id.rb_kg:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_KG);
                    }
                    break;
                case R.id.rb_fg:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_FG);
                    }
                    break;
                case R.id.rb_ml_milk:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_ML_MILK);
                    }
                    break;
                case R.id.rb_ml_water:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_ML_WATER);
                    }
                    break;
                case R.id.rb_floz_milk:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_FL_OZ_MILK);
                    }
                    break;
                case R.id.rb_floz_water:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_FL_OZ_WATER);
                    }
                    break;
                case R.id.rb_lb_lb:
                    if (binder != null) {
                        binder.setUnit(PabulumBleConfig.UNIT_LB_LB);
                    }
                    break;
            }
        }
    }

    private PabulumService.PabulumBinder binder;
    private int defaultRssi;

    private final static int DEFAULT_RSSI = -70;

    private String preWeight = "0";

    /**
     * Test
     */
    ArrayList<String> mArrayAdapter = new ArrayList<>();
    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    public ArrayList<String> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        return mArrayAdapter;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    System.out.println("**************" + deviceName);
                    System.out.println("Address : " + deviceAddress);

                }
            } catch (Exception e) {
                System.out.println("Broadcast Error : " + e.toString());
            }
        }
    };
    /**
     * /Test
     */


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.isDebug = true;
        setContentView(R.layout.main);
        //PabulumSDK.getInstance().init(this, "66617c04a3bbc7d2", "001814ae6212dd8c4657444c4b");
        PabulumSDK.getInstance().init(this);
        initData();
        ButterKnife.bind(this);
        initViews();

        if (!AppUtils.isLocServiceEnable(this)) {
            T.showShort(this, this.getString(R.string.permissions_server));
        }
        if (ensureBLESupported()) {//????????????????????????BLE???true??????????????????????????????
            initPermissions();
        }
        /**
         * Test
         */
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.registerReceiver(mReceiver, filter);
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mBtAdapter.startDiscovery();
        /**
         * /Test
         */

        reset();
    }


    private void initData() {
        defaultRssi = (int) SPUtils.get(this, Config.DEFAULT_RSSI, DEFAULT_RSSI);
    }

    private void initViews() {
        setTitleRight(Math.abs(defaultRssi));
    }

    private void reset() {
        setCurrentRssi(null);
        setBleVersion(getResources().getString(R.string.no_version));
        tvShowResult.setText(String.valueOf(preWeight));
        tvShowResult.setTextColor(getResources().getColor(R.color.black_theme));
    }


    /**
     * ??????????????????
     *
     * @param object
     */
    private void setState(Object object) {
        if (object instanceof Integer) {
            tvShowState.setText((Integer) object);
        } else if (object instanceof String) {
            tvShowState.setText((String) object);
        }
    }

    /**
     * ??????????????????
     *
     * @param object
     */
    private void setCurrentRssi(Object object) {
        if (object == null) {
            tvShowRssi.setText(R.string.no_rssi);
        } else {
            if (object instanceof Integer) {
                tvShowRssi.setText(String.format(getResources().getString(R.string.current_rssi), (Integer) object));
            }
        }
    }

    private void setBleVersion(String version) {
        tvShowVersion.setText(version);

    }


    @Override
    protected void getTimeStatus(int status) {
        //2019/4/29
        L.i(TAG, "??????????????????:" + status);
        Toast.makeText(MainActivity.this, "????????????:" + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void getCountdownStart(int time) {
        //2019/5/22
        L.i(TAG, "???????????????:" + time);
        Toast.makeText(MainActivity.this, "???????????????:" + time, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void getSynTime(byte cmdType, int timeS) {
        //2019/5/22
        String tyepName = "";
        switch (cmdType) {

            case PabulumBleConfig.SYN_TIME:
                //????????????
                tyepName = "????????????";
                break;
            case PabulumBleConfig.SYN_TIME_LESS:
                //?????????????????????
                tyepName = "?????????????????????";
                break;
            case PabulumBleConfig.TIMING_PAUSE:
                //???????????????????????????
                tyepName = "???????????????????????????";
                Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                break;
            case PabulumBleConfig.TIMING_PAUSE_LESS:
                //???????????????????????????
                tyepName = "???????????????????????????";
                Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                break;

        }
        L.i(TAG, "??????????????????:" + timeS + "||" + tyepName);
        tv_show_time.setText("TIME:" + timeS);
    }

    @Override
    protected void getPenetrateData(byte[] data) {
        L.i(TAG, "????????????:" + ParseData.arr2Str(data));

    }

    @Override
    protected void getBleDID(int did) {
        L.i(TAG, "??????did??????:" + did);
        tvShowDid.setText("DID:" + did);
    }

    /**
     * ????????????title
     *
     * @param rssi
     */
    private void setTitleRight(int rssi) {
        btnTitleRight.setText(String.format(getResources().getString(R.string.default_rssi), rssi));
    }

    @Override
    protected void onServiceBinded(BleProfileService.LocalBinder binder) {//??????????????????
        /*
        ???????????????binder??????binder??????????????????
         */
        this.binder = (PabulumService.PabulumBinder) binder;
        this.binder.getDeviceAddress();//???????????????????????????????????????
        this.binder.getDeviceName();//?????????????????????????????????
        L.e(TAG, "onServiceBinded??????????????????:" + binder);
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        switch (state) {
            case BleProfileService.STATE_CONNECTED:
                L.e(TAG, "onDeviceConnected");
                if (binder != null) {
                    setState(String.format(getResources().getString(R.string.current_device), binder.getDeviceAddress()));
                }
                break;
            case BleProfileService.STATE_DISCONNECTED:
                L.e(TAG, "onDeviceDisconnected");
                setState(R.string.disconnected);
                preWeight = "0";
                reset();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startScan();
                    }
                }, 1000);
                break;
            case BleProfileService.STATE_INDICATION_SUCCESS://????????????????????????
                L.e(TAG, "onIndicationSuccess");
                if (binder != null) {
                    binder.setUnit(preUnit);//???????????????????????????????????????APP?????????????????????????????????????????????
                }
                rgUnit.check(R.id.rb_g);
                break;
        }
    }

    @Override
    public void onError(String msg, int errorCode) {
        T.showLong(this, "msg = " + msg + "; code = " + errorCode);
    }

    private int countRssi = 0;//????????????????????????

    @Override
    public void onReadRssi(int rssi) {
        L.e(TAG, "onReadRssi rssi: " + rssi);
        setCurrentRssi(Math.abs(rssi));
        if (Math.abs(rssi) > Math.abs(defaultRssi)) {
            countRssi += 1;
        } else {
            countRssi = 0;
        }
        if (countRssi >= 20) {
            if (binder != null) {
                binder.disconnect();
            }
        }
    }

    private int countWei = 0;//????????????????????????

    @Override
    protected void getUnit(byte unitType) {//????????????????????????
        L.e(TAG, "unitType = " + unitType);
        preUnit = unitType;
        showUnit(preUnit);
    }


    @Override
    protected void getUnits(int[] units) {//?????????????????????
        L.e(TAG, "????????????????????? = " + Arrays.toString(units));
    }

    @Override
    protected void getBleVersion(String version) {
        L.e(TAG, "version = " + version);
        T.showShort(this, "?????????????????????");
        setBleVersion(String.format(getResources().getString(R.string.ble_version), version));

    }

    @Override
    protected void onLeScanCallback(BluetoothDevice device, int rssi) {
        System.out.println("*************" + device.getName());

        if (rssi >= defaultRssi) {
            connectDevice(device);
        }
    }

    @Override
    protected void onStartScan() {
        setState(R.string.scan_ing);
    }

    @Override
    protected void bluetoothStateOn() {//???????????????
        super.bluetoothStateOn();
        setState(R.string.ble_state_on);
        L.e(TAG, "bluetoothStateOn");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        }, 500);
    }

    private byte preUnit = PabulumBleConfig.UNIT_G;
    private boolean isBleChangeUnit = false;

    @Override
    protected void getFoodData(FoodData foodData) {
        if (foodData == null) {//2017-06-16??????????????????
            return;
        }
        L.e(TAG, "weight = " + foodData.getData());
        String curWeight = foodData.getData();
        if (TextUtils.equals(curWeight, preWeight)) {
            countWei += 1;
        } else {
            countWei = 0;
            preWeight = curWeight;
        }
        if (countWei >= 5) {
            tvShowResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvShowResult.setTextColor(getResources().getColor(R.color.black_theme));
        }
        if (foodData.getUnit() != preUnit) {
            preUnit = foodData.getUnit();
            isBleChangeUnit = true;
            showUnit(preUnit);
        }
        String unitStr = getUnitStr(preUnit);

        tvShowResult.setText(preWeight + " " + unitStr + "\nType:" + foodData.getDeviceType() + "\n" + foodData.getWeight() + "g");
    }


    private void showUnit(int preUnit) {
        switch (preUnit) {
            case PabulumBleConfig.UNIT_G:
                rgUnit.check(R.id.rb_g);
                break;
            case PabulumBleConfig.UNIT_ML:
                rgUnit.check(R.id.rb_ml);
                break;
            case PabulumBleConfig.UNIT_LB:
                rgUnit.check(R.id.rb_lb);
                break;
            case PabulumBleConfig.UNIT_OZ:
                rgUnit.check(R.id.rb_oz);
                break;
            case PabulumBleConfig.UNIT_KG:
                rgUnit.check(R.id.rb_kg);
                break;
            case PabulumBleConfig.UNIT_FG:
                rgUnit.check(R.id.rb_fg);
                break;
            case PabulumBleConfig.UNIT_ML_MILK:
                rgUnitTwo.check(R.id.rb_ml_milk);
                break;
            case PabulumBleConfig.UNIT_ML_WATER:
                rgUnitTwo.check(R.id.rb_ml_water);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_MILK:
                rgUnitTwo.check(R.id.rb_floz_milk);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_WATER:
                rgUnitTwo.check(R.id.rb_floz_water);
                break;
            case PabulumBleConfig.UNIT_LB_LB:
                rgUnitTwo.check(R.id.rb_lb_lb);
                break;
        }
    }


    private String getUnitStr(int preUnit) {
        String unitStr = getString(R.string.unit_g);
        switch (preUnit) {
            case PabulumBleConfig.UNIT_G:
                unitStr = getString(R.string.unit_g);
                break;
            case PabulumBleConfig.UNIT_ML:
                unitStr = getString(R.string.unit_ml);
                break;
            case PabulumBleConfig.UNIT_LB:
                unitStr = getString(R.string.unit_lb_oz);
                break;
            case PabulumBleConfig.UNIT_OZ:
                unitStr = getString(R.string.unit_oz);
                break;
            case PabulumBleConfig.UNIT_KG:
                unitStr = getString(R.string.unit_kg);
                break;
            case PabulumBleConfig.UNIT_FG:
                unitStr = getString(R.string.unit_fg);
                break;
            case PabulumBleConfig.UNIT_ML_MILK:
                unitStr = getString(R.string.unit_ml_milk);
                break;
            case PabulumBleConfig.UNIT_ML_WATER:
                unitStr = getString(R.string.unit_ml_water);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_MILK:
                unitStr = getString(R.string.unit_oz_milk);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_WATER:
                unitStr = getString(R.string.unit_oz_water);
                break;
            case PabulumBleConfig.UNIT_LB_LB:
                unitStr = getString(R.string.unit_lb);
                break;
        }
        return unitStr;
    }

    @Override
    protected void bluetoothStateOff() {//???????????????
        super.bluetoothStateOff();
        setState(R.string.ble_state_off);
        L.e(TAG, "bluetoothStateOff");
    }

    @Override
    protected void bluetoothTurningOff() {//??????????????????
        super.bluetoothTurningOff();
        L.e(TAG, "bluetoothTurningOff");
    }

    @Override
    protected void bluetoothTurningOn() {//??????????????????
        super.bluetoothTurningOn();
        L.e(TAG, "bluetoothTurningOn");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e(TAG, "onDestroy");
        stopScan();
        if (binder != null) {
            binder.disconnect();//???Activity??????????????????????????????????????????????????????????????????
        }
    }

    private Handler handler = new Handler();

    private Runnable disconnectRunnable = new Runnable() {
        @Override
        public void run() {
            binder.disconnect();
        }
    };

    @Override
    public void query(int rssi) {
        defaultRssi = rssi;
        setTitleRight(Math.abs(defaultRssi));
        if (binder != null) {
            binder.disconnect();
        } else {
            startScan();
        }
    }

    @Override
    protected void onWriteSuccess(byte[] value) {
        L.e(TAG, "onWriteSuccess: " + ParseData.arr2Str(value));
    }


    /**
     * ?????????????????????
     */
    private void initPermissions() {
        /**
         * @Ali
         * Added Manifest.permission.ACCESS_FINE_LOCATION to permission request to give fine location access.
         */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startScan();//????????????

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //?????????????????????????????????????????????????????????
                new AlertDialog.Builder(this).setTitle(this.getString(R.string.hint)).setMessage(this.getString(R.string.permissions))
                        .setPositiveButton(this.getString(R.string.query), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //????????????????????????????????????
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                }).show();
            } else {
                //???????????????????????????????????????????????????
//                T.showShort(MainActivity.this, "??????????????????");
                new AlertDialog.Builder(this).setTitle(this.getString(R.string.hint)).setMessage(this.getString(R.string.permissions))
                        .setPositiveButton(this.getString(R.string.query), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //????????????????????????????????????
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);

                            }
                        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                }).show();
            }

        }

    }

}
