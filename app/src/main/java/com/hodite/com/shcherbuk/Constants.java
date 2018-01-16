package com.hodite.com.shcherbuk;

public interface Constants {
    public static final String URL_HODITE_COM = "http://google.com/";  //URL сайта
    public static final String URL_TEXT_TXT = "http://hodite.com/not/text.txt";  //URL text.txt
    //for youtube
    public static final String API_KEY = "AIzaSyABTsN9_n_H5R0vipOim3L3EMXYWXUolk8";
    public static final String VIDEO_ID = "oQiz-lojGP8";
    //TAGS
    public static final String TAG_MAIN = "MainActivity";
    public static final String TAG_WEB = "WebActivity";
    public static final String TAG_START = "StartActivity";
    public static final String TAG_MYSERVICE = "MyService";

    public static final String KEY_INTENT = "URL";  //Ключ для получения сайта по putExtra Intent
    public static final int NOTIFICATION_KEY = 1;
    public static final int NOTIFY_ID = 101;


    /*НАСТРОЙКИ+СОХРАНЕНИЕ ИНФЫ*/
    public static final String CHECK_SETTINGS = "check_settings"; //Имя файла
    //Обучение
    public static final String hasWathed="false"; // проверка на просмотр обучения

    //Уведомления
    public static final String notif_text="notif_text"; // Текст уведомления
    public static final String notif_url="notif_url"; // Ссылка уведомления

    public static final long SERVIC_NOTIFY_INTERVAL = 1 * 10 * 1000; // 10 seconds


}
