package romanow.abc.tnsk.android.menu;

import java.io.File;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.I_ArchiveMultiSelector;
import romanow.abc.tnsk.android.I_EventListener;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.SetOneParameter;
import romanow.abc.tnsk.android.service.AppData;


public class MIGroupCreate extends MenuItem {
    public MIGroupCreate(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Группировать") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Группировать",toGroupSelector);
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector toGroupSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(final FileDescriptionList fd, boolean longClick) {
            new SetOneParameter(main,"Группа","",true, new I_EventListener() {
                @Override
                public void onEvent(String subdir) {
                    if (AppData.SubDirList.get(subdir)!=null){
                        main.popupAndLog(subdir+" зарезервировано для программы");
                        return;
                        }
                    File dd = new File(AppData.ctx().androidFileDirectory()+"/"+subdir);
                    if (dd.exists()){
                        main.popupAndLog(subdir+" уже существует");
                        return;
                        }
                    dd.mkdir();
                    for (FileDescription ff : fd){
                        try {
                            String src = AppData.ctx().androidFileDirectory()+"/"+ff.getOriginalFileName();
                            main.moveFile(src, AppData.ctx().androidFileDirectory()+"/"+subdir+"/"+ff.getOriginalFileName());
                            }
                        catch (Exception ee){ main.errorMes(main.createFatalMessage(ee,5)); }
                        }
                    main.popupAndLog("Сгруппировано в "+subdir);
                }
            });
        }
    };
}
