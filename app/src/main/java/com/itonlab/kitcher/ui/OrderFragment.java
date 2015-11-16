package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class OrderFragment extends Fragment {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private KitcherDao databaseDao;
    private ListView listViewOrder;
    private ArrayList<Order> orders;
    private OrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        // initial database access object
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getActivity());
                JsonFunction.Message appMessage = JsonFunction.acceptMessage(message);
                if (appMessage.getMessageType().equals(JsonFunction.Message.Type.ORDER_MESSAGE)) {
                    Order order = jsonFunction.acceptOrderFromClient(appMessage);

                    // put "new order" to ListView
                    orders.add(0, order);
                    orderListAdapter.notifyDataSetChanged();
                    listViewOrder.post(new Runnable() {
                        public void run() {
                            listViewOrder.smoothScrollToPosition(listViewOrder.getCount() - 1);
                        }
                    });
                } else {
                    jsonFunction.decideWhatToDo(appMessage);
                }
            }
        });

        listViewOrder = (ListView) rootView.findViewById(R.id.listViewOrder);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = orders.get(position);
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        server.start();
        databaseDao.open();
        // เพราะทุกครั้งที่กลับมาหน้านี้ให้โหลดข้อมูลมาแสดงใหม่ทุกครั้ง
        orders = databaseDao.getAllOrderNotServed();
        orderListAdapter = new OrderListAdapter(getActivity(), orders, R.layout.order_list_item);
        listViewOrder.setAdapter(orderListAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
        databaseDao.close();
    }

}
