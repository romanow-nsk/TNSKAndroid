package romanow.abc.tnsk.android.service;

import romanow.abc.core.utils.GPSPoint;
import romanow.abc.tnsk.android.MainActivity;

public interface I_GPSService {
    public void startService(MainActivity main0);
    public void stopService();
    public GPSPoint lastGPS();
    }
