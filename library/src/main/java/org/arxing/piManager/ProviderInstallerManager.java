package org.arxing.piManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

/**
 * android 4.4(API 19)以下不支持 TLS1.1 及 TLS1.2
 * 會造成訪問https時拋出 {@link javax.net.ssl.SSLHandshakeException} 錯誤
 * 使用此類來檢查/安裝SSL
 */
public class ProviderInstallerManager {
    private Activity activity;
    private Callback callback;

    public ProviderInstallerManager(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    /**
     * 檢查是否需要安裝SSL, 若需要則安裝並回調於callback
     */
    public void keepSSLAvailable() {
        ProviderInstaller.installIfNeededAsync(activity, listener);
    }

    public void showInstallFailedDialog(int requestCode, int errorCode) {
        if (GoogleApiAvailability.getInstance().isUserResolvableError(errorCode)) {
            DialogInterface.OnCancelListener cancelListener = dialog -> callback.onProviderInstallerNotAvailable();
            GoogleApiAvailability.getInstance().showErrorDialogFragment(activity, errorCode, requestCode, cancelListener);
        } else {
            callback.onProviderInstallerNotAvailable();
        }
    }

    private ProviderInstaller.ProviderInstallListener listener = new ProviderInstaller.ProviderInstallListener() {
        @Override public void onProviderInstalled() {
            callback.onProviderInstallerAvailable();
        }

        @Override public void onProviderInstallFailed(int errorCode, Intent intent) {
            showInstallFailedDialog(0, 0);
        }
    };

    public interface Callback {
        void onProviderInstallerNotAvailable();

        void onProviderInstallerAvailable();
    }
}
