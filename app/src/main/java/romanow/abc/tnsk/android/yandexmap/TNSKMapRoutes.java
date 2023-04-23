package romanow.abc.tnsk.android.yandexmap;

import java.util.ArrayList;

import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TRouteStop;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.entity.subjectarea.TStop;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;

public class TNSKMapRoutes extends TNSKMapActivity{
    @Override
    public void onBroadCastMode(){
        TRoute route = AppData.ctx().route();
        for(int idx=0; idx<route.getSegments().size();idx++) {
            TSegment segment = route.getSegments().get(idx).getSegment().getRef();
            ArrayList<GPSPoint> points = new ArrayList<>();
            for (int i = 0; i < segment.getPoints().size(); i++) {
                points.add(segment.getPoints().get(i).getGps());
                }
            paint("Сегмент "+idx, points, drawId, false, 0, null);
            }
        int idx=0;
        for(TRouteStop stop : route.getStops()){
            TStop stop2 = stop.getStop().getRef();
            paint(stop2.getName(),stop2.getGps(), R.drawable.busstop_min,idx==route.getStops().size()-1);
            idx++;
            }
        }
}
