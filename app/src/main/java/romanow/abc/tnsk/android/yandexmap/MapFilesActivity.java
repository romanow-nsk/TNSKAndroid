package romanow.abc.tnsk.android.yandexmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.R;

public class MapFilesActivity extends MapActivity340 {
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GPSPoint gps = new GPSPoint();
            int state = intent.getIntExtra("state",GPSPoint.GeoNone);
            gps.setCoord(intent.getDoubleExtra("geoY",0),
                    intent.getDoubleExtra("geoX",0),false);
            gps.state(state);
            paintSelf();
            }
        };
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_GPS);
        this.registerReceiver(gpsReceiver, filter);
        }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gpsReceiver);
        }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paintSelf();
        moveToSelf();
        ArrayList<FileDescription> list = AppData.ctx().getFileList();
        for(FileDescription file : list)
            paint(file.toString(),file.getGps(),R.drawable.mappoint,true);
        }
    }
