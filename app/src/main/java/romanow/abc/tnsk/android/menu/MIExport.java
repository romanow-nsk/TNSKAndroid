package romanow.abc.tnsk.android.menu;

import java.io.FileInputStream;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.I_ArchiveMultiSelector;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.service.AppData;

public class MIExport extends MenuItem {
    public MIExport(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Экспорт в Excel") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Экспорт в Excel",exportSelector);
                }
            });
        }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector exportSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList flist, boolean longClick) {
            for(FileDescription fd : flist) {
                try {
                    String pathName = AppData.ctx().androidFileDirectory() + "/" + fd.getOriginalFileName();
                    FileInputStream fis = new FileInputStream(pathName);
                    } catch (Throwable e) {
                        main.errorMes("Файл не открыт: " + fd.getOriginalFileName() + "\n" + main.createFatalMessage(e, 10));
                        }
                }
            }
        };
    }

