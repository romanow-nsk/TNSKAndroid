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

public class MIMapCareNearest extends MenuItem {
    private AppData ctx;
    public MIMapCareNearest(MainActivity base){
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
                        ctx.sendGPSMode(AppData.GPSModeCaresNearest,"",0);
                        /*------------------------------------- Точки через broadcast
                        for(TCare care : cares){
                            ctx.sendGPS(care.getTitle(AppData.ctx().getCareTypeMap())+" "+care.lastPoint().getSpeed()+" км/ч",care.lastPoint().getGps(),R.drawable.taxi_min,false);
                            }
                        TPassenger passenger = AppData.ctx().passenger();
                        for(TPassengerPoint point : passenger.getPassengerStory()){
                            ctx.sendGPS(passenger.getUser().getTitle()+" "+point.getGps().geoTime().timeToString(),point.getGps(), point.isOnBoard() ? R.drawable.on_care_min : R.drawable.on_walk_min,false);
                            }
                        */
                        }
                    });
                }
            });
        }
    }

