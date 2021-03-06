package com.hayashi.android_gps_sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager = null;
    private String locationProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        requestPermissionIfNot(this);

        // 位置情報を管理している LocationManager のインスタンスを生成
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.locationProvider = getLocationProvider(this, this.locationManager);
        requestLocationUpdates(this, this.locationManager, this.locationProvider);

        Location location = getLocation(locationManager, locationProvider);
        if (location != null) {
            TextView textView = findViewById(R.id.textResult);
            textView.setText(String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
        }
    }

    private static Location getLocation(LocationManager locationManager, String locationProvider) {
        Location location = null;
        try{
            location = locationManager.getLastKnownLocation(locationProvider);
        }catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }

    private static void requestLocationUpdates(LocationListener locationListener, LocationManager locationManager, String locationProvider) {
        // 位置情報の通知するための最小時間間隔（ミリ秒）
        final long minTime = 500;
        // 位置情報を通知するための最小距離間隔（メートル）
        final long minDistance = 1;

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        try{
            locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, locationListener);
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static String getLocationProvider(Activity activity, LocationManager locationManager) {
        String locationProvider = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPSが利用可能になっている場合
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっている場合
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            // いずれも利用可能でない場合は、GPSを設定する画面に遷移
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(settingsIntent);
        }
        return locationProvider;
    }

    public static void requestPermissionIfNot(Activity activity) {
        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある
        Boolean isFineGranted =
                ActivityCompat.checkSelfPermission(activity.getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        Boolean isCoarseGranted =
                ActivityCompat.checkSelfPermission(activity.getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (!isFineGranted && !isCoarseGranted) {
            // fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い）
            final int requestCode = 1;
            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    requestCode );
        }
    }

    // ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String provider) {
    }

    // 位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location){
    }

    // ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String provider) {
    }
}
