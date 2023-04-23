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
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.core.entity.subjectarea.TSegment;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.I_ListBoxListener;
import romanow.abc.tnsk.android.ListBoxDialog;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;

public class MIMapSegments extends MenuItem {
    private AppData ctx;
    public MIMapSegments(MainActivity base){
        super(base);
        ctx = AppData.ctx();
        AppSettings set = ctx.loginSettings();
        if (ctx.cState()!= AppData.CStateGreen){
            base.popupAndLog("Сервер недоступен");
            return;
            }
        new NetCall<EntityRefList<TSegment>>().call(base,ctx.getService2().getSegments(set.getSessionToken()), new NetBackDefault<EntityRefList<TSegment>>(){
            @Override
            public void onSuccess(EntityRefList<TSegment> oo) {
                ctx.setSegments(oo);
                startMap(new Runnable() {
                    @Override
                    public void run() {
                        ctx.sendGPSMode(AppData.GPSModeSegments,"",0);
                        }
                    });
                }
            });
        }
    }

