package romanow.abc.tnsk.android.yandexmap;

import java.util.ArrayList;

import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;

public class TNSKMapCaresNearest extends TNSKMapActivity{
    @Override
    public void onBroadCastMode(){
        for(TCare care : AppData.ctx().getCares()){
            paint(care.getTitle(AppData.ctx().getCareTypeMap())+" "+care.lastPoint().getSpeed()+" км/ч",care.lastPoint().getGps(),R.drawable.taxi_min,false);
            }
        TPassenger passenger = AppData.ctx().passenger();
        for(TPassengerPoint point : passenger.getPassengerStory()){
            paint(passenger.getUser().getTitle()+" "+point.getGps().geoTime().timeToString(), point.getGps(),point.isOnBoard() ? R.drawable.on_care_min : R.drawable.on_walk_min,false);
        }
        }
}
