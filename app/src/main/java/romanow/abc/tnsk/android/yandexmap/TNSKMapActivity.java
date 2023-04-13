package romanow.abc.tnsk.android.yandexmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.R;

public class TNSKMapActivity extends MapActivity340 {
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("current",true))
                return;
            GPSPoint gps = new GPSPoint();
            int state = intent.getIntExtra("state",GPSPoint.GeoNone);
            String title = intent.getStringExtra("title");
            if (title==null)
                title="...";
            int drawId = intent.getIntExtra("drawId",R.drawable.mappoint);
            gps.setCoord(intent.getDoubleExtra("gpsY",0),
                    intent.getDoubleExtra("gpsX",0),false);
            gps.state(state);
            boolean moveTo = intent.getBooleanExtra("moveTo",false);
            paint(title,gps,drawId,moveTo);
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
        }
    }