package romanow.abc.tnsk.android.menu;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TRouteStop;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.core.entity.subjectarea.TStop;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.I_ListBoxListener;
import romanow.abc.tnsk.android.ListBoxDialog;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;

public class MIMapRoutes extends MenuItem {
    private AppData ctx;
    public MIMapRoutes(MainActivity base){
        super(base);
        ctx = AppData.ctx();
        AppSettings set = ctx.loginSettings();
        if (ctx.cState()!= AppData.CStateGreen){
            base.popupAndLog("Сервер недоступен");
            return;
            }
        new NetCall<ArrayList<DBRequest>>().call(base,ctx.getService().getEntityList(set.getSessionToken(),
                "TRoute", ValuesBase.GetAllModeActual,0), new NetBackDefault<ArrayList<DBRequest>>(){
            @Override
            public void onSuccess(ArrayList<DBRequest> oo) {
                final ArrayList<TRoute> routes = new ArrayList<>();
                final ArrayList<String> names = new ArrayList<>();
                HashMap<Integer, ConstValue> map = Values.constMap().getGroupMapByValue("RouteType");
                Gson gson = new Gson();
                for(DBRequest request : oo) {
                    try {
                        TRoute route = (TRoute) request.get(gson);
                        routes.add(route);
                        names.add(route.getTitle(map)+" "+route.getStopName1()+" - "+route.getStopName2());
                        } catch (UniException e) {
                            ctx.toLog(false,"Ошибка JSON для маршрутов: "+e.toString());
                            return;
                            }
                        }
                new ListBoxDialog(base, names, "Маршруты", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {
                        loadRoute(routes.get(index),names.get(index));
                        }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                    }).setnLines(3).create();
                }
            });
        }
    private void loadRoute(TRoute route0, String title){
        new NetCall<TRoute>().call(main,ctx.getService2().getRoute(ctx.loginSettings().getSessionToken(),
                route0.getOid()), new NetBackDefault<TRoute>(){
            @Override
            public void onSuccess(final TRoute route) {
                startMap(new Runnable() {
                    @Override
                    public void run() {
                        ctx.route(route);                       // Через AppData
                        ctx.sendGPSMode(AppData.GPSModeRoute,title,R.drawable.where);
                        /*------------------  Точки через broadcast
                        for(TRouteStop stop : route.getStops()){
                            TStop  stop2 = stop.getStop().getRef();
                            ctx.sendGPS(stop2.getGps(),stop2.getName(),R.drawable.busstop_min,false);
                            }
                        for(int idx=0; idx<route.getSegments().size();idx++){
                            TSegment segment = route.getSegments().get(idx).getSegment().getRef();
                            for(int i=0;i<segment.getPoints().size();i++){
                                int mode = AppData.GPSModeNext;
                                if (i==0)
                                    mode = AppData.GPSModeFirst;
                                if (i==segment.getPoints().size()-1)
                                    mode = AppData.GPSModeLast;
                                ctx.sendGPS(segment.getPoints().get(i).getGps(),"Сегмент "+i,0,false,mode);
                                }
                            }
                        */
                        }
                });
            }
        });
        }
    }

