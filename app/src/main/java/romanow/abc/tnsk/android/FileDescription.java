package romanow.abc.tnsk.android;

import romanow.abc.core.utils.GPSPoint;
import romanow.abc.core.utils.OwnDateTime;

public class FileDescription {
    public FileDescription(String fname) {
        this.fname = fname; }
    private String fname;
    private OwnDateTime createDate = new OwnDateTime();
    private GPSPoint gps = new GPSPoint();
    public String getOriginalFileName(){ return fname; }
    public OwnDateTime getCreateDate() {
        return createDate; }
    public GPSPoint getGps() {
        return gps;  }
}
