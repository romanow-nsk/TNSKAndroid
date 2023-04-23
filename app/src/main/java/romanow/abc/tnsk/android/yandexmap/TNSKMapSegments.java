package romanow.abc.tnsk.android.yandexmap;

import android.content.Intent;

import java.util.ArrayList;

import romanow.abc.core.Pair;
import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TSegmentStatistic;
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TRouteStop;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.entity.subjectarea.TStop;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.graph.FullScreenGraph;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;

public class TNSKMapSegments extends TNSKMapActivity{
    @Override
    public void onBroadCastMode(){
        final EntityRefList<TSegment> segments = AppData.ctx().getSegments();
        for(int idx=0; idx<segments.size();idx++) {
            final TSegment segment = segments.get(idx);
            ArrayList<GPSPoint> points = new ArrayList<>();
            for (int i = 0; i < segment.getPoints().size(); i++) {
                points.add(segment.getPoints().get(i).getGps());
                }
            paint("Сегмент " + idx, points, drawId, false, idx, new I_MapSelect() {
                @Override
                public void onSelect(int idx) {
                    final AppData ctx = AppData.ctx();
                    new NetCall<TSegmentStatistic>().call(TNSKMapSegments.this,ctx.getService2().getSegmentStatistic(ctx.loginSettings().getSessionToken(),
                            segments.get(idx).getOid()), new NetBackDefault<TSegmentStatistic>(){
                        @Override
                        public void onSuccess(TSegmentStatistic oo) {
                            ctx.popupAndLog(false,"ячеек "+oo.getNotNullCellCount()+" значений "+oo.getTotalValues());
                            ctx.getStatList().add(new Pair<>(""+segment.getOid(),oo));
                            Intent intent = new Intent();
                            intent.setClass(TNSKMapSegments.this.getApplicationContext(), FullScreenGraph.class);
                            startActivity(intent);
                            }
                    });
                    }
                });
            }
        moveToSelf();
        }
}
