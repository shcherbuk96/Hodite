package com.hodite.com.shcherbuk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Constants{
    String[] cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

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
                    case 0://Минск
                        Toast.makeText(getApplicationContext(),"Данный город отсутствует",Toast.LENGTH_SHORT).show();
                        break;
                    case 1://Брест
                        //Toast.makeText(getApplicationContext(),cities[pos],Toast.LENGTH_SHORT).show();
                        loadWebActivity(URL_HODITE_COM);
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
                .setMessage("Ты уверен,что хочешь выйти?")
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
}
