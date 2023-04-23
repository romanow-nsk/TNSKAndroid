package romanow.abc.tnsk.android.yandexmap;

import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;

public class TNSKMapPassenger extends TNSKMapActivity{
    @Override
    public void onBroadCastMode(){
        TPassenger passenger = AppData.ctx().passenger();
        int size= passenger.getPassengerStory().size();
        int idx=0;
        for(TPassengerPoint point : passenger.getPassengerStory()){
            paint(point.getGps().geoTime().timeToString(),point.getGps(),
                    point.isOnBoard() ? R.drawable.on_care_min : R.drawable.on_walk_min, idx==size-1);
            idx++;
            }
        }
}
