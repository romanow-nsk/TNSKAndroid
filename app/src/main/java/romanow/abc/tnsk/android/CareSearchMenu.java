package romanow.abc.tnsk.android;

import android.content.Intent;

import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.menu.MenuItem;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;
import romanow.abc.tnsk.android.yandexmap.CaresMapActivity;

public class CareSearchMenu extends MenuItem {
    private AppData ctx;
    public CareSearchMenu(MainActivity base){
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
                base.popupAndLog("Найдено "+cares.size()+" бортов");
                for(TCare care : cares){
                    base.addToLog(care.toStringFull(ctx.getCareTypeMap()));
                    }
                ctx.setCares(cares);
                Intent intent = new Intent();
                intent.setClass(main.getApplicationContext(), CaresMapActivity.class);      // Стартануть карту
                main.startActivity(intent);
                }
            });
        }
    }

