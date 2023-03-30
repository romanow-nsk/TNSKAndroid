package romanow.abc.tnsk.android.menu;

import java.io.File;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.I_ArchiveMultiSelector;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.service.AppData;

public class MIDeleteFromArchive extends MenuItem {
    public MIDeleteFromArchive(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Удалить из архива") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Удалить из архива",deleteMultiSelector);
            }
        });
    }
    //-----------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector deleteMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            for (FileDescription ff : fd){
                File file = new File(AppData.ctx().androidFileDirectory()+"/"+ff.getOriginalFileName());
                file.delete();
                }
            }
    };
}
