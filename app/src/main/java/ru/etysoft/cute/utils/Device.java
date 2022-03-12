package ru.etysoft.cute.utils;

import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.RegisterDeviceRequest;
import ru.etysoft.cuteframework.storage.Cache;

public class Device {

    public interface DeviceCallback {
        void onRegistered();
        void onError();
    }

    public static void registerDevice(DeviceCallback deviceCallback)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RegisterDeviceRequest registerDeviceRequest = new RegisterDeviceRequest(android.os.Build.MODEL, "mobile");
                try {
                    RegisterDeviceRequest.RegisterDeviceResponse registerDeviceResponse = registerDeviceRequest.execute();
                    if(registerDeviceResponse.isSuccess())
                    {
                        deviceCallback.onRegistered();
                    }
                    else
                    {
                        deviceCallback.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deviceCallback.onError();
                }

            }
        });
        thread.start();
    }

    public static boolean isDeviceRegistered()
    {
        return Cache.getUserAccount().hasDeviceId();
    }


}
