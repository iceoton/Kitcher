package com.itonlab.kitcher.ui;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.MenuTable;
import com.itonlab.kitcher.model.Picture;


public class EditDatabaseActivity  extends Activity{
    private int menuId;
    private KitcherDao databaseDao;
    private MenuItem menuItem;
    private ImageView imageViewFood;
    private EditText etName, etPrice;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_database);

        databaseDao = new KitcherDao(EditDatabaseActivity.this);
        databaseDao.open();

        imageViewFood = (ImageView) findViewById(R.id.ivImgFood);
        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        btnSave = (Button) findViewById(R.id.btnSave);

        menuId = getIntent().getIntExtra(MenuTable.Columns._ID, 0);
        Log.d("DATABASE", "menu_id = " + menuId);
        if (menuId != 0) {
            menuItem = databaseDao.getMenuAtId(menuId);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveToDatabase();
                }
            });
        } else {
            // to add new data
            menuItem = new MenuItem();
            btnSave.setText(getResources().getText(R.string.text_add));
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDataToDatabase();
                }
            });
        }

        Picture pictureFood = databaseDao.getMenuPicture(menuItem.getPictureId());
        imageViewFood.setImageBitmap(pictureFood.getBitmapPicture());
        etName.setText(menuItem.getNameThai());
        etPrice.setText(String.valueOf(menuItem.getPrice()));
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
        finish();
    }

    private void addDataToDatabase() {
        String menuName = etName.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        if (!menuName.equals("")) {
            menuItem.setNameThai(menuName);
            menuItem.setPrice(price);
            databaseDao.addMenu(menuItem);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter menu name...", Toast.LENGTH_SHORT).show();
        }
    }
}
