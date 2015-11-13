package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.DatabaseListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.MenuTable;

import java.util.ArrayList;

public class ShowDatabaseActivity extends Activity {
    private KitcherDao databaseDao;
    private ListView lvData;
    private Button btnAddData;
    private ArrayList<MenuItem> menuItems;
    DatabaseListAdapter databaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_database);

        databaseDao = new KitcherDao(ShowDatabaseActivity.this);
        databaseDao.open();

        lvData = (ListView) findViewById(R.id.listData);
        menuItems = databaseDao.getMenu();
        lvData.setOnItemClickListener(listDataOnItemClick);

        btnAddData = (Button) findViewById(R.id.btnAdd);
        btnAddData.setOnClickListener(addDataOnclickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseDao.open();
        menuItems = databaseDao.getMenu();
        databaseListAdapter = new DatabaseListAdapter(ShowDatabaseActivity.this, menuItems);
        lvData.setAdapter(databaseListAdapter);
    }

    @Override
    protected void onStop() {
        databaseDao.close();
        super.onStop();
    }

    AdapterView.OnItemClickListener listDataOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final int menuId = (int) id;
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDatabaseActivity.this);
            builder.setMessage("ต้องการลบหรือแก้ไข?")
                    .setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Delete data in the database.
                            databaseDao.deleteMenu(menuId);

                            // Remove item from ListView.
                            menuItems.remove(position);
                            databaseListAdapter.notifyDataSetChanged();

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("แก้ไข", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ShowDatabaseActivity.this, EditDatabaseActivity.class);
                            intent.putExtra(MenuTable.Columns._ID, menuId);
                            startActivity(intent);
                        }
                    });
            // Create the AlertDialog object
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

    View.OnClickListener addDataOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ShowDatabaseActivity.this, EditDatabaseActivity.class);
            intent.putExtra(MenuTable.Columns._ID, 0);
            startActivity(intent);
        }
    };

}
