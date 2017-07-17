package com.qbasso.nfcreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qbasso.nfcreader.R;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SplashActivity extends AppCompatActivity {

    private Disposable launchMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        launchMain = Observable.fromCallable(new Callable<Intent>() {
            @Override
            public Intent call() throws Exception {
                return new Intent(SplashActivity.this, NfcReaderActivity.class);
            }
        }).delay(60, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(@NonNull Intent o) throws Exception {
                        finish();
                        startActivity(o);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (launchMain != null && !launchMain.isDisposed()) {
            launchMain.dispose();
        }
    }
}
