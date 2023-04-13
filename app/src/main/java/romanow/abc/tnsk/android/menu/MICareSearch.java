package romanow.abc.tnsk.android.menu;

import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;

public class MICareSearch extends MenuItem {
    private AppData ctx;
    public MICareSearch(MainActivity base){
        super(base);
        ctx = AppData.ctx();
        AppSettings set = ctx.loginSettings();
        if (ctx.cState()!= AppData.CStateGreen){
            base.popupAndLog("Сервер недоступен");
            return;
            }
        GPSPoint gps = ctx.getLastGPS();
        if (!gps.gpsValid()){
            base.popupAndLog("Нет GPS-координат");
            return;
            }
        if(gps.state()==GPSPoint.GeoNet){
            base.popupAndLog("Нет точных GPS-координат");
            return;
            }
        new NetCall<EntityRefList<TCare>>().call(base,ctx.getService2().getNearestCares(set.getSessionToken(),set.getSearchCareDistantion(), gps), new NetBackDefault<EntityRefList<TCare>>(){
            @Override
            public void onSuccess(EntityRefList<TCare> cares) {
                final AppData ctx = AppData.ctx();
                base.popupAndLog("Найдено "+cares.size()+" бортов");
                for(TCare care : cares){
                    base.addToLog(care.toStringFull(ctx.getCareTypeMap()));
                    }
                ctx.setCares(cares);
                startMap(new Runnable() {
                    @Override
                    public void run() {
                        for(TCare care : cares){
                            ctx.sendGPS(care.lastPoint().getGps(),care.getTitle(AppData.ctx().getCareTypeMap())+" "+care.lastPoint().getSpeed()+" км/ч",R.drawable.taxi,false);
                            }
                        TPassenger passenger = AppData.ctx().passenger();
                        for(TPassengerPoint point : passenger.getPassengerStory()){
                            ctx.sendGPS(point.getGps(),passenger.getUser().getTitle(), point.onCare() ? R.drawable.on_care : R.drawable.on_walk,false);
                            }
                        }
                    });
                }
            });
        }
    }

