package romanow.abc.tnsk.android.menu;

import android.content.Intent;
import android.os.Handler;

import romanow.abc.tnsk.android.MainActivity;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.yandexmap.TNSKMapActivity;

public class MenuItem {
    protected MainActivity main;
    public MenuItem(MainActivity main0){
        main = main0;
    }
    public void startMap(Runnable delayed){
        Intent intent = new Intent();
        intent.setClass(main.getApplicationContext(), TNSKMapActivity.class);      // Стартануть карту
        main.startActivity(intent);
        final Handler handler = new Handler();
        handler.postDelayed(delayed,AppData.MapStartDelay*1000);
        }
    }
