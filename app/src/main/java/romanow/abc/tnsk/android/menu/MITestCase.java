package romanow.abc.tnsk.android.menu;

import romanow.abc.tnsk.android.MainActivity;

public class MITestCase extends MenuItem {
    public MITestCase(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Отладка") {
            @Override
            public void onSelect() {
                Thread thread = Thread.currentThread();
                Integer aaa=null;
                aaa.intValue();
            }
        });
    }

}
