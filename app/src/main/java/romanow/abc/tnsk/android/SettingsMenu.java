package romanow.abc.tnsk.android;

import android.widget.LinearLayout;

import romanow.abc.tnsk.android.service.AppData;

public class SettingsMenu extends SettingsMenuBase {
    public SettingsMenu(MainActivity base0){
        super(base0);
        }
    @Override
    public void settingsSave() {
        base.saveContext();
        }

    @Override
    public void createDialog(LinearLayout trmain){
        try {
            AppSettings set = AppData.ctx().loginSettings();
            LinearLayout layout = createItem("Mail ", ""+set.getMailToSend(), true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setMailToSend(ss);
                    settingsChanged();
                    }
                });
            trmain.addView(layout);
            layout = createItem("АвтоКоннект", set.isAutoConnect() ? "1" : "0" , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setAutoConnect(Integer.parseInt(ss)!=0);
                        settingsChanged();
                        base.overLoad(true);
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                        }
                });
            trmain.addView(layout);
            layout = createItem("Поиск борта (м)", ""+set.getSearchCareDistantion() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setSearchCareDistantion(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("История gps (час)", ""+set.getPassengerStoryHours() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setPassengerStoryHours(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Откл. от маршрута (м)", ""+set.getRouteDistance() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setRouteDistance(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Расст. борт-пасс.(м)", ""+(int)set.getCarePassDistance() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setCarePassDistance(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Скорости борт-пасс.(км/ч)", ""+(int)set.getSpeedDiff() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setSpeedDiff(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Пешком пасс.(км/ч)<", ""+(int)set.getSpeedMax() , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setSpeedMax(Integer.parseInt(ss));
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            //---------------------------------------------------------------------------------------
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
}

