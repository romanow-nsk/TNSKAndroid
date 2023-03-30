package romanow.abc.tnsk.android.menu;


import static romanow.abc.tnsk.android.MainActivity.ViewProcHigh;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.I_ArchiveMultiSelector;
import romanow.abc.tnsk.android.MainActivity;

public class MIArchive extends MenuItem {
    public MIArchive(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Архив") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Проcмотр архива",procViewMultiSelector);
            }
        });
    }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procViewMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
            for (FileDescription ff : fd){
                main.procArchive(ff,false);
                }
            }
    };
}
