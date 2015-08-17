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

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class SettingsFragment extends Fragment{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;

    private TextView textViewIP, textViewStatus;
    private EditText editTextMessage, editTextIP;
    private Button buttonSend;
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

        textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus);

        textViewIP = (TextView) rootView.findViewById(R.id.textViewIP);
        textViewIP.setText(TCPUtils.getIP(getActivity()));

        editTextMessage = (EditText) rootView.findViewById(R.id.editTextMessage);

        editTextIP = (EditText) rootView.findViewById(R.id.editTextIP);
        TCPUtils.forceInputIP(editTextIP);

        buttonSend = (Button) rootView.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editTextMessage.getText().length() > 0) {
                    String message = editTextMessage.getText().toString();
                    String ip = editTextIP.getText().toString();

                    SimpleTCPClient.send(message, ip, TCP_PORT, new SimpleTCPClient.SendCallback() {
                        public void onSuccess(String tag) {
                            textViewStatus.setText("Status : Sent");
                        }

                        public void onFailed(String tag) {
                            textViewStatus.setText("Status : Failed");
                        }
                    }, "TAG");

                    editTextMessage.setText("");
                    textViewStatus.setText("Status : Sending...");
                }
            }
        });

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
        server.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
    }
}
