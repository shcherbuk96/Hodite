package com.hodite.com.shcherbuk.MainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hodite.com.shcherbuk.ActivityManager;
import com.hodite.com.shcherbuk.Constants;
import com.hodite.com.shcherbuk.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Constants {
    private SharedPreferences sp;
    private boolean[] mCheckedItems;
    private Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        context=this;

        if (getIntent().getExtras() != null) {
            ActivityManager.startWebActivity(context, getIntent().getStringExtra("URL"));
            finish();
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout); //Переход с затуханием
        }

        final ListView list = findViewById(R.id.list_select_city);
        final String[] cities = getResources().getStringArray(R.array.list_cities);

        // используем адаптер данных
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, cities);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

//                final TextView textView = (TextView) view;
//                final String strText = textView.getText().toString();

                switch (pos) {
                    case 0://Брест
                        //Toast.makeText(getApplicationContext(),cities[pos],Toast.LENGTH_SHORT).show();
                        ActivityManager.startWebActivity(context, URL_HODITE_COM);
                        finish();

                        break;
                    case 1://Минск
                        Toast.makeText(getApplicationContext(), "Данный город отсутствует", Toast.LENGTH_SHORT).show();

                        break;
                    case 2://Витебск
                        Toast.makeText(getApplicationContext(), "Данный город отсутствует", Toast.LENGTH_SHORT).show();

                        break;
                    case 3://Гомель
                        Toast.makeText(getApplicationContext(), "Данный город отсутствует", Toast.LENGTH_SHORT).show();

                        break;
                    case 4://Гродно
                        Toast.makeText(getApplicationContext(), "Данный город отсутствует", Toast.LENGTH_SHORT).show();

                        break;
                    case 5://Могилёв
                        Toast.makeText(getApplicationContext(), "Данный город отсутствует", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface arg0, final int arg1) {
                        //finish();
                        //эмулируем нажатие на HOME, сворачивая приложение
                        final Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final String[] items = {" Получать уведомления об обновлениях сайта ", " Получать уведомления об акциях магазинов "};

        final List<String> colorsList = Arrays.asList(items);

        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        final boolean webSite = sp.getBoolean(notifWebSite, true);
        final boolean shops = sp.getBoolean(notifShops, true);

        mCheckedItems = new boolean[]{webSite, shops};


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Настройки")
                .setMultiChoiceItems(items, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int indexSelected, final boolean isChecked) {
                        mCheckedItems[indexSelected] = isChecked;

                        // Get the current focused item
                        final String currentItem = colorsList.get(indexSelected);

                        Toast.makeText(getApplicationContext(),
                                currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();

                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        final SharedPreferences.Editor e = sp.edit();
                        e.putBoolean(notifWebSite, mCheckedItems[0]);
                        e.putBoolean(notifShops, mCheckedItems[1]);
                        e.commit(); // не забудьте подтвердить изменения

                        if (mCheckedItems[0]) {
                            FirebaseMessaging.getInstance().subscribeToTopic("WEB");
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("WEB");
                        }

                        if (mCheckedItems[1]) {
                            FirebaseMessaging.getInstance().subscribeToTopic("SHOP");
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("SHOP");
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        //  Your code when user clicked on Cancel
                        dialog.cancel();
                    }
                }).create();
        dialog.show();

        return true;
    }
}
