package romanow.abc.tnsk.android.menu;

import android.content.Intent;

import java.io.BufferedReader;
import java.util.ArrayList;

import romanow.abc.tnsk.android.FileDescription;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.I_ArchiveMultiSelector;
import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.yandexmap.CaresMapActivity;


public class MIMap extends MenuItem {
    public MIMap(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Показать на карте") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Показать на карте",procMapMultiSelector);
                }
            });
        }
    //----------------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procMapMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            try {
                FileDescriptionList list = AppData.ctx().getFileList();
                list.clear();
                for (FileDescription ff : fd){
                    BufferedReader reader = main.openReader(ff.getOriginalFileName());
                    reader.close();
                    if (ff.getGps().gpsValid())
                        list.add(ff);
                    }
                Intent intent = new Intent();
                intent.setClass(main.getApplicationContext(), CaresMapActivity.class);
                main.startActivity(intent);
            } catch (Exception ee){ main.errorMes(ee.toString());}
        }
    };

}
