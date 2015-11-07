package com.itonlab.kitcher.ui;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.MenuTable;


public class EditDatabaseActivity  extends Activity{
    private int menuId;
    private KitcherDao databaseDao;
    private MenuItem menuItem;
    private EditText etName, etPrice;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_database);

        databaseDao = new KitcherDao(EditDatabaseActivity.this);
        databaseDao.open();

        menuId = getIntent().getIntExtra(MenuTable.Columns._ID,0);
        Log.d("DATABASE", "menu_id = " + menuId);

        menuItem = databaseDao.getMenuAtId(menuId);
        etName = (EditText) findViewById(R.id.etName);
        etName.setText(menuItem.getNameThai());
        etPrice = (EditText) findViewById(R.id.etPrice);
        etPrice.setText(String.valueOf(menuItem.getPrice()));

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseDao.close();
    }

    private void saveToDatabase(){
        String menuName = etName.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        menuItem.setNameThai(menuName);
        menuItem.setPrice(price);
        databaseDao.updateMenu(menuItem);
    }
}
