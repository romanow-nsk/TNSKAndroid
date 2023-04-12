package romanow.abc.tnsk.android.service;

import romanow.abc.core.UniException;

public interface NetBack<T> {
    public void onError(int code, String mes);
    public void onError(UniException ee);
    public void onSuccess(T val);
    }
