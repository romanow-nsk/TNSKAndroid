package romanow.abc.tnsk.android;

import romanow.abc.core.UniException;
import romanow.abc.tnsk.android.service.NetBack;

public abstract class NetBackProxy implements NetBack {
    private NetBack origin;
    public NetBackProxy(NetBack orig){
        origin = orig;
        }
    @Override
    public void onError(int code, String mes) {
        origin.onError(code,mes);
        }
    @Override
    public void onError(UniException ee) {
        origin.onError(ee);
        }
};
