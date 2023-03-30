package romanow.abc.tnsk.android.service;

import romanow.abc.core.utils.GPSPoint;

public interface GPSListener {
    public void onEvent(String ss);
    public void onGPS(GPSPoint gpsPoint);
}
