package com.hodite.com.shcherbuk;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Constants{
    String[] cities;
    SharedPreferences sp;
    boolean[] mCheckedItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        if(getIntent().getExtras()!=null) {
            Intent intent = new Intent(getApplicationContext(), WebActivity.class);
            intent.putExtra(KEY_INTENT, getIntent().getStringExtra("URL"));
            startActivity(intent);
            overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
/*            if (getIntent().getExtras().containsKey("WEB")) {
                Log.i("INTENT","WEB");
                intent.putExtra(KEY_INTENT, getIntent().getStringExtra("WEB"));
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
            }
            if (getIntent().getExtras().containsKey("SHOP")) {
                Log.i("INTENT","SHOP");
                intent.putExtra(KEY_INTENT, getIntent().getStringExtra("SHOP"));
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
            }*/
        }

//        if(!isMyServiceRunning(MyService.class)){
//            startService(new Intent(this,MyService.class));
//        }

        ListView list=(ListView)findViewById(R.id.list_select_city);
        cities=getResources().getStringArray(R.array.list_cities);

        // используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, cities);
        list.setAdapter(adapter);
        //list.setDivider(getResources().getDrawable(android.R.color.transparent));//убрать разделители
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                TextView textView=(TextView)view;
                String strText=textView.getText().toString();

                switch (pos){
                    case 0://Брест
                        //Toast.makeText(getApplicationContext(),cities[pos],Toast.LENGTH_SHORT).show();
                        loadWebActivity(URL_HODITE_COM);
                        break;
                    case 1://Минск
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                    case 2://Витебск
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                    case 3://Гомель
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                    case 4://Гродно
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                    case 5://Могилёв
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
    public void loadWebActivity(String URL){
        Intent intent=new Intent(getApplicationContext(),WebActivity.class);
        intent.putExtra(KEY_INTENT,URL);
        //Запуск Браузера
        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //finish();
                        //эмулируем нажатие на HOME, сворачивая приложение
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

//в зависимости от версии оси намертво убиваем приложение
/*                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            finishAndRemoveTask();
                        }
                        else
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                finishAffinity();
                            } else
                            {
                                finish();
                            }
                        }*/

                        //System.exit(0);
                        //MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] items = {" Получать уведомления об обновлениях сайта "," Получать уведомления об акциях магазинов "};

        final List<String> colorsList = Arrays.asList(items);

        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        boolean webSite = sp.getBoolean(notifWebSite, true);
        boolean shops = sp.getBoolean(notifShops, true);

        mCheckedItems= new boolean[]{webSite, shops};


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Настройки")
                .setMultiChoiceItems(items, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        mCheckedItems[indexSelected]=isChecked;

                        // Get the current focused item
                        String currentItem = colorsList.get(indexSelected);

                        Toast.makeText(getApplicationContext(),
                                currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();


                        /*if(indexSelected==0){
                            Log.d("Settings","notifWebSite"+isChecked);
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean(notifWebSite, isChecked);
                            e.commit(); // не забудьте подтвердить изменения
                        }
                        if(indexSelected==1){
                            Log.d("Settings","notifShops"+isChecked);
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean(notifShops, isChecked);
                            e.commit(); // не забудьте подтвердить изменения
                        }*/
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor e = sp.edit();
                        e.putBoolean(notifWebSite, mCheckedItems[0]);
                        e.putBoolean(notifShops, mCheckedItems[1]);
                        e.commit(); // не забудьте подтвердить изменения

                        if(mCheckedItems[0]==true){
                            FirebaseMessaging.getInstance().subscribeToTopic("WEB");
                        }else if (mCheckedItems[0]==false){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("WEB");
                        }

                        if(mCheckedItems[1]==true){
                            FirebaseMessaging.getInstance().subscribeToTopic("SHOP");
                        }else if (mCheckedItems[1]==false){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("SHOP");
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                        dialog.cancel();
                    }
                }).create();
        dialog.show();

//        startActivity(new Intent(getApplicationContext(),Settings.class));
        return true;
    }

    /**-----------------------------ПРОВЕРКА НА ЗАПУСК СЕРВИСА--------------------**/
/*    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/
    /*----------------------------------------------------------------------------------*/



    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onStop() {
        super.onStop();

    }


}
