package romanow.abc.tnsk.android.menu;

import java.util.ArrayList;

import romanow.abc.core.ErrorList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TCarePoint;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.I_ListBoxListener;
import romanow.abc.tnsk.android.ListBoxDialog;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;
import romanow.abc.tnsk.android.yandexmap.TNSKMapPassenger;

public class MIMapPassenger extends MenuItem {
    private AppData ctx;
    public MIMapPassenger(MainActivity base){
        super(base);
        ctx = AppData.ctx();
        AppSettings set = ctx.loginSettings();
        if (ctx.cState()!= AppData.CStateGreen){
            base.popupAndLog("Сервер недоступен");
            return;
            }
        final GPSPoint gps = ctx.getLastGPS();
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
            public void onSuccess(final EntityRefList<TCare> cares) {
                final AppData ctx = AppData.ctx();
                base.popupAndLog("Найдено "+cares.size()+" бортов");
                ctx.setCares(cares);
                ArrayList<String> careNames = new ArrayList<>();
                for(TCare care : cares){
                    base.addToLog(care.toStringFull(ctx.getCareTypeMap()));
                    careNames.add(care.getTitle(ctx.getCareTypeMap())+" "+(int)gps.diff(care.getGps())+" м "+(int)care.lastPoint().getSpeed()+" км/ч");
                    }
                new ListBoxDialog(base, careNames, "История борта", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {
                        loadCareStory(cares.get(index));
                        }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                }).create();
                }
            });
        }
        //------------------------------------------------------------------------------------------
        private void loadCareStory(TCare care){
            AppSettings set = AppData.ctx().loginSettings();
            new NetCall<TCare>().call(main,ctx.getService2().getCareStory(set.getSessionToken(),care.getCareKey()), new NetBackDefault<TCare>(){
                @Override
                public void onSuccess(final TCare care1) {
                    //----------- Данные в ctx.passenger()
                    ErrorList fin = care.searchInRoute2(ctx.passenger(),set.getRouteDistance(),set.getCarePassDistance(),set.getSpeedDiff(),set.getSpeedMax());
                    main.addToLog(fin.toString());
                    main.addToLog(ctx.passenger().getRouteInfo().toString());
                    startMap(TNSKMapPassenger.class,new Runnable() {
                        @Override
                        public void run() {
                            ctx.sendGPSMode(AppData.GPSModePassenger,"",0);
                            }
                        });
                    }
                });
            }
    }

