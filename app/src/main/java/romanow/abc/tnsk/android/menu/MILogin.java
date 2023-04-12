package romanow.abc.tnsk.android.menu;

import android.widget.LinearLayout;

import com.google.gson.Gson;

import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.WorkSettings;
import romanow.abc.core.entity.users.Account;
import romanow.abc.core.entity.users.User;
import romanow.abc.tnsk.android.I_EventListener;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.SettingsMenuBase;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.NetBack;
import romanow.abc.tnsk.android.service.NetBackDefault;
import romanow.abc.tnsk.android.service.NetCall;

public class MILogin extends SettingsMenuBase {
    public MILogin(MainActivity base0){
        super(base0);
        }
    private AppData ctx;
    @Override
    public void settingsSave() {
        ctx.fileService().saveJSON(ctx.loginSettings());
        }

    @Override
    public void createDialog(LinearLayout trmain) {
        ctx = AppData.ctx();
        try {
            final AppSettings set = AppData.ctx().loginSettings();
            LinearLayout layout = createItem("IP", set.getDataSetverIP(), true,true,
                    new String[]{"217.71.138.9","192.168.0.225"},new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setDataSetverIP(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            layout = createItem("Порт", ""+set.getDataServerPort(), false,false,
                    new String[]{"4567","4569","4571","5001"},new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setDataServerPort(Integer.parseInt(ss));
                        settingsChanged();
                        } catch (Exception ee){
                           base.popupInfo("Формат числа");}
                            }
                    });
            trmain.addView(layout);
            layout = createItem("Телефон", set.getUserPhone(),true,false,
                    new String[]{"9335555555"},new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPhone(ss);
                    settingsChanged();
                    }});
            trmain.addView(layout);
            layout = createItem("Пароль", "******", true,true,
                    new String[]{"1234","112233"},new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPass(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            final boolean isLogin = ctx.cState()== AppData.CStateGreen;
            layout = createItem((!isLogin ? "Войти" : "Выйти"), "",true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    if (!isLogin){
                        login(base);
                        }
                    else{
                        new NetCall<JEmpty>().call(base,ctx.getService().logoff(ctx.loginSettings().getSessionToken()), new NetBackDefault(){
                            @Override
                            public void onSuccess(Object val) {
                                base.sessionOff();
                                ctx.cState(AppData.CStateGray);
                                }
                            });
                        }
                    cancel();
                    }
                });
            trmain.addView(layout);
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
    public static void login(MainActivity base){
        AppData ctx = AppData.ctx();
        base.retrofitConnect();
        final AppSettings set = AppData.ctx().loginSettings();
            Account acc = new Account("",set.getUserPhone(), set.getUserPass());
            new NetCall<User>().call(base,ctx.getService().login(acc), new NetBack(){
                @Override
                public void onError(int code, String mes) {
                    if (code == Values.HTTPAuthorization)
                        ctx.toLog(false,"Ошибка авторизации: "+mes+"");
                    else if (code==Values.HTTPNotFound)
                        ctx.toLog(false,"Ошибка соединения: "+mes+"");
                    else
                        ctx.toLog(false,mes);
                    }
                @Override
                public void onError(UniException ee) {
                    ctx.popupToastFatal(ee);
                }
                @Override
                public void onSuccess(Object val) {
                    base.sessionOn();
                    User user =(User)val;
                    final AppSettings set = ctx.loginSettings();
                    set.setUserId(user.getOid());
                    set.setSessionToken(user.getSessionToken());
                    base.putHeaderInfo(set.getDataSetverIP()+"\n"+user.shortUserName()+"\n"+user.typeName());
                    new NetCall<DBRequest>().call(base,ctx.getService().workSettings(ctx.loginSettings().getSessionToken()), new NetBackDefault() {
                        @Override
                        public void onSuccess(Object val) {
                            try {
                                ctx.workSettings((WorkSettings)((DBRequest)val).get(new Gson()));
                                ctx.setRegisteredOnServer(true);
                            } catch (UniException e) {
                                base.errorMes("Загрузка параметров сервера:\n"+e.toString());
                                }
                            }
                        });
                    }
                });
            }
}

