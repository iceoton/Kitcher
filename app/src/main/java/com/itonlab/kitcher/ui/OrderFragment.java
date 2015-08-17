package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itonlab.kitcher.R;

public class OrderFragment extends Fragment {

    private ListView listViewOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order,container, false);

        listViewOrder = (ListView)rootView.findViewById(R.id.listViewOrder);

        return rootView;
    }

}
