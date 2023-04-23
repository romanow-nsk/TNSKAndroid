package romanow.abc.tnsk.android.yandexmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;

import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TCarePoint;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TRouteStop;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.entity.subjectarea.TStop;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.R;

public class TNSKMapActivity extends MapActivityBase {
    private ArrayList<GPSPoint> points = new ArrayList<>();
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
            int mode = intent.getIntExtra("mode",AppData.GPSModeNone);
            AppData ctx = AppData.ctx();
            TPassenger passenger=null;
            switch (mode){
                case AppData.GPSModeNone:
                    paint(title,gps,drawId,moveTo);
                    break;
                case AppData.GPSModeFirst:
                    points.clear();
                    points.add(gps);
                    break;
                case AppData.GPSModeNext:
                    points.add(gps);
                    break;
                case AppData.GPSModeLast:
                    points.add(gps);
                    paint(title,points,drawId,moveTo,0,null);
                    break;
                case AppData.GPSModeRoute:
                    TRoute route = AppData.ctx().route();
                    for(int idx=0; idx<route.getSegments().size();idx++) {
                        TSegment segment = route.getSegments().get(idx).getSegment().getRef();
                        points = new ArrayList<>();
                        for (int i = 0; i < segment.getPoints().size(); i++) {
                            points.add(segment.getPoints().get(i).getGps());
                            }
                        paint("Сегмент "+idx, points, drawId, false, 0, null);
                        }
                    int idx=0;
                    for(TRouteStop stop : route.getStops()){
                        TStop stop2 = stop.getStop().getRef();
                        paint(stop2.getName(),stop2.getGps(),R.drawable.busstop_min,idx==route.getStops().size()-1);
                        idx++;
                        }
                    break;
                case AppData.GPSModeSegments:
                    EntityRefList<TSegment> segments = ctx.getSegments();
                    for(idx=0; idx<segments.size();idx++) {
                        TSegment segment = segments.get(idx);
                        points = new ArrayList<>();
                        for (int i = 0; i < segment.getPoints().size(); i++) {
                            points.add(segment.getPoints().get(i).getGps());
                            }
                        paint("Сегмент "+idx, points, drawId, false, 0, null);
                        }
                    break;
                case AppData.GPSModePassenger:
                    passenger = AppData.ctx().passenger();
                    int size= passenger.getPassengerStory().size();
                    idx=0;
                    for(TPassengerPoint point : passenger.getPassengerStory()){
                        paint(point.getGps().geoTime().timeToString(),point.getGps(),
                                point.isOnBoard() ? R.drawable.on_care_min : R.drawable.on_walk_min, idx==size-1);
                        idx++;
                        }
                    break;
                case AppData.GPSModeCaresNearest:
                    for(TCare care : ctx.getCares()){
                        paint(care.getTitle(AppData.ctx().getCareTypeMap())+" "+care.lastPoint().getSpeed()+" км/ч",care.lastPoint().getGps(),R.drawable.taxi_min,false);
                        }
                    passenger = AppData.ctx().passenger();
                    for(TPassengerPoint point : passenger.getPassengerStory()){
                        paint(passenger.getUser().getTitle()+" "+point.getGps().geoTime().timeToString(), point.getGps(),point.isOnBoard() ? R.drawable.on_care_min : R.drawable.on_walk_min,false);
                        }
                    break;
                case AppData.GPSModeCareStory:
                    TCare care = ctx.getCare();
                    idx=1;
                    size= care.getCareStory().size();
                    for(TCarePoint point : care.getCareStory()){
                        paint(care.getTitle(AppData.ctx().getCareTypeMap())+" "+point.getSpeed()+" км/ч "+point.getGps().geoTime().timeToString(),
                                point.getGps(),idx==size ? R.drawable.taxi_min : R.drawable.where, idx==size);
                        idx++;
                        }
                    break;
                    }
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
