package com.itonlab.kitcher.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.FoodOrder;
import com.itonlab.kitcher.util.AppPreference;
import com.itonlab.kitcher.util.OrderFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class SettingsFragment extends Fragment{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private KitcherDao databaseDao;
    private TextView textViewIP;
    private EditText editTextName;
    private LinearLayout layoutEditDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings,container, false);
        // initial database access object
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                Log.d("JSON", message);
                OrderFunction orderFunction = new OrderFunction(getActivity());
                orderFunction.acceptJSONOrder(message);
            }
        });

        textViewIP = (TextView) rootView.findViewById(R.id.textViewIP);
        textViewIP.setText(TCPUtils.getIP(getActivity()));

        editTextName = (EditText) rootView.findViewById(R.id.etYourName);

        layoutEditDatabase = (LinearLayout)rootView.findViewById(R.id.layoutEditDatabase);
        layoutEditDatabase.setOnClickListener(layoutEditDatabaseListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettingsValue();
        server.start();
        databaseDao.open();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettingsValue();
        server.stop();
        databaseDao.close();
    }

    private void saveSettingsValue(){
        String yourName = editTextName.getText().toString().trim();

        AppPreference appPreference = new AppPreference(getActivity());
        appPreference.saveYourName(yourName);
    }

    private void loadSettingsValue(){
        AppPreference appPreference = new AppPreference(getActivity());
        String yourName = appPreference.getYourName();
        editTextName.setText(yourName);
    }

    View.OnClickListener layoutEditDatabaseListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            showDialogPassword();
        }
    };

    private void showDialogPassword(){

        final Dialog dialogLogin = new Dialog(getActivity());
        dialogLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLogin.setContentView(R.layout.dialog_login);
        dialogLogin.setCancelable(true);
        dialogLogin.show();

        final EditText etPassword  = (EditText) dialogLogin.findViewById(R.id.etPassword);
        Button btnLogin = (Button) dialogLogin.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference appPreference = new AppPreference(getActivity());
                String savePassword = appPreference.getAppPassword();
                String inputPassword = etPassword.getText().toString().trim();
                if (inputPassword.equals(savePassword)) {
                    //Logged in
                    Intent intent = new Intent(getActivity(), ShowDatabaseActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });


    }
}
