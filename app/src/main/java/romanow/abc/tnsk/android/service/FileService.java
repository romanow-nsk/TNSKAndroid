package romanow.abc.tnsk.android.service;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import romanow.abc.core.entity.base.BugList;
import romanow.abc.core.entity.WorkSettings;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.StoryList;

public class FileService {
    private AppData ctx;
    public FileService(AppData ctx0){
        ctx = ctx0;
        }
    //--------------------------------------------------------------------------------------
    public String fileName(Class clazz) {
        String ss = ctx.androidFileDirectory() + "/" + clazz.getSimpleName() + ".json";
        return ss;
        }
    public void saveJSON(Object oo)  {
        try {
            if (oo == null){
                String ss = "Ошибка сохранения локальных данных (null-объект)";
                ctx.popupAndLog(true,ss);
                ctx.createBugMessage(ss);
                return;
            }
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName(oo.getClass())), "UTF-8");
            gson.toJson(oo, out);
            out.flush();
            out.close();
            //xs.toXML(oo,out);
        } catch (Exception ee) {
            ctx.popupToastFatal(ee);

        }
    }

    public Object loadJSON(Class clazz) {
        boolean bad=false;
        Object ent=null;
        Exception ee = null;
        try {
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            InputStreamReader out = new InputStreamReader(new FileInputStream(fileName(clazz)), "UTF-8");
            ent = gson.fromJson(out, clazz);
            out.close();
            bad = ent == null;
            } catch (Exception ex) {
                bad = true;
                ee =ex;
                }
            if (!bad)
                return ent;
            try {
                String s0 = "Cоздан новый "+clazz.getSimpleName();
                String ss = s0+"\n"+(ee==null ? "" : ee.toString()+"\n"+ee.getMessage());
                ctx.popupAndLog(true,s0);
                if (clazz != BugList.class)
                    ctx.addBugMessage(ss);
                Object zz = clazz.newInstance();
                saveJSON(zz);
                return zz;         // Создать пустой, если не читается
                } catch (Exception e) {
                    String zz = "Не могу создать объект "+clazz.getSimpleName();
                    ctx.popupAndLog(true,zz);
                    if (clazz != BugList.class)
                        ctx.addBugMessage(zz);
                    return null;
                    }

    }
    public void saveContext(){
        AppData ctx = AppData.ctx();
        saveJSON(ctx.loginSettings());
        saveJSON(ctx.fatalMessages());
        saveJSON(ctx.storyList());
        saveJSON(ctx.workSettings());
        }
    public void loadContext(){
        AppData ctx = AppData.ctx();
        ctx.loginSettings((AppSettings)loadJSON(AppSettings.class));
        ctx.workSettings((WorkSettings)loadJSON(WorkSettings.class));
        ctx.storyList((StoryList)loadJSON(StoryList.class));
        ctx.fatalMessages((BugList) loadJSON(BugList.class));
    }

}
