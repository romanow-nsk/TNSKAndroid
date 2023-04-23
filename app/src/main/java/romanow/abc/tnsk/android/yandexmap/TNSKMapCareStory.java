package romanow.abc.tnsk.android.yandexmap;

import java.util.ArrayList;

import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TCarePoint;
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TRouteStop;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.entity.subjectarea.TStop;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;

public class TNSKMapCareStory extends TNSKMapActivity{
    @Override
    public void onBroadCastMode(){
        TCare care = AppData.ctx().getCare();
        int idx=1;
        int size= care.getCareStory().size();
        for(TCarePoint point : care.getCareStory()){
            paint(care.getTitle(AppData.ctx().getCareTypeMap())+" "+point.getSpeed()+" км/ч "+point.getGps().geoTime().timeToString(),
                    point.getGps(),idx==size ? R.drawable.taxi_min : R.drawable.where, idx==size);
            idx++;
            }
        }
}
