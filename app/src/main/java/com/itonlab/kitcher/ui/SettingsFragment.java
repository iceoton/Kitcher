package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.util.AppPreference;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class SettingsFragment extends Fragment{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;

    private TextView textViewIP;
    private EditText editTextName;
    private ListView listViewChat;

    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings,container, false);

        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                data.add(message);
                adapter.notifyDataSetChanged();
                listViewChat.post(new Runnable() {
                    public void run() {
                        listViewChat.smoothScrollToPosition(listViewChat.getCount() - 1);
                    }
                });
            }
        });

        textViewIP = (TextView) rootView.findViewById(R.id.textViewIP);
        textViewIP.setText(TCPUtils.getIP(getActivity()));

        editTextName = (EditText) rootView.findViewById(R.id.etYourName);

        data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity()
                , android.R.layout.simple_list_item_1, data);

        listViewChat = (ListView) rootView.findViewById(R.id.listViewChat);
        listViewChat.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettingsValue();
        server.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettingsValue();
        server.stop();

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
}
