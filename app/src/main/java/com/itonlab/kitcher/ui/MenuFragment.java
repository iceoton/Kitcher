package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.MenuListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class MenuFragment extends Fragment {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private KitcherDao databaseDao;
    ListView lvFood;
    ArrayList<MenuItem> menuItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initial database access object
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getActivity());
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
            }
        });
        View rootView = inflater.inflate(R.layout.fragment_menu,container, false);
        lvFood = (ListView)rootView.findViewById(R.id.listFood);

        menuItems = databaseDao.getMenu();
        MenuListAdapter menuListAdapter = new MenuListAdapter(getActivity(), menuItems);
        lvFood.setAdapter(menuListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        server.start();
        databaseDao.open();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
        databaseDao.close();
    }
}
