package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.FoodListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.FoodItem;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    ListView lvFood;
    private KitcherDao databaseDao;
    ArrayList<FoodItem> foodItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();

        View rootView = inflater.inflate(R.layout.fragment_menu,container, false);
        lvFood = (ListView)rootView.findViewById(R.id.listFood);

        foodItems = databaseDao.getMenu();
        FoodListAdapter foodListAdapter = new FoodListAdapter(getActivity(),foodItems);
        lvFood.setAdapter(foodListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        databaseDao.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        databaseDao.close();
        super.onPause();
    }

}
