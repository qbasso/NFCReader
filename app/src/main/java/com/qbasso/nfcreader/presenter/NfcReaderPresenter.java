package com.qbasso.nfcreader.presenter;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.qbasso.nfcreader.api.BackendClientHelper;
import com.qbasso.nfcreader.api.BackendService;
import com.qbasso.nfcreader.view.NfcReaderView;

import java.nio.charset.Charset;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class NfcReaderPresenter implements Presenter {

    private final NfcReaderView view;
    public static final String AUTH_TOKEN = "KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c";
    private static String PAYLOAD_UNLOCK = "unlock";
    public static final String CHARSET = "US-ASCII";
    private BackendService backendService;
    private Disposable disposable;

    public NfcReaderPresenter(NfcReaderView view) {
        this.view = view;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void destroy() {

    }

    public void init() {
        backendService = BackendClientHelper.getBackendService();
    }

    public void unlock(String value) {
        if (value.equals(PAYLOAD_UNLOCK)) {
            disposable = backendService.unlock(AUTH_TOKEN).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Response<Void>>() {
                @Override
                public void accept(@NonNull Response<Void> voidResponse) throws Exception {
                    if (voidResponse.isSuccessful()) {
                        view.openAnimation();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {

                }
            });
        }
    }

    public void processMessage(NdefMessage message) {
        NdefRecord[] records = message.getRecords();
        if (records != null && records.length == 1) {
            String value = new String(records[0].getPayload(), Charset.forName(CHARSET));
            unlock(value);
        }
    }
}
