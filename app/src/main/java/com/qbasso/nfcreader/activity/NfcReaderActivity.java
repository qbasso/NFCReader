package com.qbasso.nfcreader.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.qbasso.nfcreader.R;
import com.qbasso.nfcreader.fragment.DrawerFragment;
import com.qbasso.nfcreader.presenter.NfcReaderPresenter;
import com.qbasso.nfcreader.view.NfcReaderView;
import com.qbasso.nfcreader.widget.LockView;

public class NfcReaderActivity extends AppCompatActivity implements NfcReaderView {

    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private String[][] techLists;
    private ActionBarDrawerToggle toggle;
    private LockView padlock;
    private NfcReaderPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);
        presenter = new NfcReaderPresenter(this);
        presenter.init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        padlock = (LockView) findViewById(R.id.lockView);
        setupDrawer();
        enableForgroundDispatch();
        checkForNfcTag(getIntent());
    }

    private void enableForgroundDispatch() {
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        filters = new IntentFilter[]{
                ndef,
        };
        techLists = new String[][]{new String[]{NfcF.class.getName(), NfcA.class.getName(), NfcB.class.getName(), NfcV.class.getName(), Ndef.class.getName()}};
    }

    private void setupDrawer() {
        Fragment f = DrawerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.drawer, f, null).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.empty, R.string.empty);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                return true;
            default:
                return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.enableForegroundDispatch(this, pendingIntent, filters, techLists);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Nfc disabled!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (adapter == null) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkForNfcTag(intent);
    }

    private void checkForNfcTag(Intent intent) {
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage message = rawMessages.length == 1 ? (NdefMessage) rawMessages[0] : null;
                if (message != null) {
                    processMessage(message);
                }
            }
        }
    }

    private void processMessage(NdefMessage message) {
        presenter.processMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void openAnimation() {
        padlock.openAnimation();
    }
}
