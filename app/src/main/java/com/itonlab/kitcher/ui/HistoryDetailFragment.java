package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.HistoryDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryDetailFragment extends Fragment{
    private KitcherDao databaseDao;
    private ArrayList<Order> orders;
    private ListView listViewOrder;
    private HistoryDetailListAdapter historyDetailListAdapter;

    private Button btnSummary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_detail,container, false);

        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();

        orders = databaseDao.getAllOrderServed();
        listViewOrder = (ListView) rootView.findViewById(R.id.listViewOrder);
        historyDetailListAdapter = new HistoryDetailListAdapter(getActivity(), orders);
        listViewOrder.setAdapter(historyDetailListAdapter);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // click to see order detail.
                int orderId = (int) id;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss", Locale.getDefault());
                String orderTime = dateFormat.format(orders.get(position).getOrderTime());
                Intent intent = new Intent(getActivity(), HistoryOrderDetailActivity.class);
                intent.putExtra(OrderTable.Columns._ID, orderId);
                intent.putExtra(OrderTable.Columns._ORDER_TIME, orderTime);
                getActivity().startActivity(intent);
            }
        });

        btnSummary = (Button) rootView.findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HistoryFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseDao.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseDao.close();
    }
}
