package com.tresk.maniv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.app.Application;
import android.util.Log;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerConversionListener;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    WebView view;
    WebSettings webSettings;
    static NetworkInfo netInfo;

    int count = 0;
    String urlhost = "";
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "myurl";
    public static final String APP_PREFERENCES_URL = "url";
    String urGogo = "https://5mincredit.com.ua";
    String deepPartone = "";
    String namingSl = "";
    String afStatus = "";
    SharedPreferences.Editor editor;
    // end.
    private static final String AF_DEV_KEY = "qLBVL3yAsrHaY8PgyhfLBW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                String c = "campaign";
                String as = "af_status";
                for (String attrName : conversionData.keySet()) {

                    if (c.equals(attrName)) {
                        namingComp = (String) conversionData.get(attrName);

                        Log.e("mylog:appflyer_IFFF_one", "nameC ="+ namingComp);
                    }
                    if (as.equals(attrName)) {
                        afStatus = (String) conversionData.get(attrName);

                        Log.e("mylog:appflyer_IFFF_two", "afStatus ="+ afStatus);
                    }

                    Log.d("mylog:appflyer", "attribute: " + attrName + " =!! " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("mylog:appflyer", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                String linkA = "";
                String li = "link";

                for (String attrName : attributionData.keySet()) {
                    if (attrName.equals(li) && attributionData.get(attrName) != null) {
                        linkA = attributionData.get(attrName);
                        Log.d("mylog:appflyer000", "attribute: " + attrName + " = " + linkA+ " SIze"+ attributionData.size());
                        String[] dParts = linkA.split("\\?");
                        deepPart ="?"+ dParts[1];
                        deepPart =  deepPart.replace("{UID}", appsFlyerId);

                        Log.d("mylog:appflyer000", "deeplink part=" + deepPart);
                    }
                    Log.d("mylog:appflyer222", "attribute: " + attrName + " = " + attributionData.get(attrName));

                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("mylog", "error onAttributionFailure : " + errorMessage);
            }

        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
        AppsFlyerLib.getInstance().startTracking(this);


        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_URL)) {
            urGogo = mSettings.getString(APP_PREFERENCES_URL, "");
            Log.e("mylog:URL сохранённый", urGogo);
        }
        // для ФБ диплинки
        Intent intent = getIntent();
        Uri data = intent.getData();

        try {
            urlhost = data.getQuery();
            urlhost = "?" + urlhost;
            Log.i("mylog", "переменная urlhost" + urlhost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isOnlineNet(this)) {

            try {
                Vnutr();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {

        }
    }
    public void Vnutr () throws InterruptedException {
        count = 1;
        view = (WebView) findViewById(R.id.tersk);

        view.post(new Runnable() {
            @Override
            public void run() {

                webSettings = view.getSettings();
                webSettings.setJavaScriptEnabled(true); //включаем выполнение яваскрипт
                webSettings.setDomStorageEnabled(true); //для сохранение куки
                webSettings.setBuiltInZoomControls(true); //управление сжатием(масштабированием) экрана пальцами
                webSettings.setSupportZoom(true);

                webSettings.setDisplayZoomControls(false); //оключение масштабирования кнопками на экране

                view.setInitialScale(1); //для не адаптированых сайтов полное отображение
                webSettings.setLoadWithOverviewMode(true); //что бы сайт не был сжат
                webSettings.setUseWideViewPort(true); //поддержка ViewPort(узнать) на сайтах под мобилы


                view.getSettings().setAllowContentAccess(true);
                view.getSettings().setAllowFileAccess(true);


                view.loadUrl(urGogo);

               // view.addJavascriptInterface(new mersrattgiging(), "HTMLOUT");

                view.setWebViewClient(new WebViewClient() {

                                             public void onPageFinished(WebView view, String url) {

                                                 CookieSyncManager.getInstance().sync();
                                                 urGogo = view.getUrl();
                                                 Log.e("mylog_onPageFinishedURL",""+ urGogo); //при заливе закоментить
                                                 editor = mSettings.edit();
                                                 editor.putString(APP_PREFERENCES_URL, urGogo);
                                                 editor.apply();
                                             }

                                             public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                                                 return false;
                                             }

                                         }
                );
            }
        });

    }
    @Override
    public void onBackPressed() {
        if (count == 1) {
            if (view.canGoBack()) {
                //Log.e("НАЖАЛИ НАЗАД ИФ", String.valueOf(counter));
                view.goBack();
            } else {

                //Log.e("НАЖАЛИ НАЗАД ЕЛСЕ", String.valueOf(counter));
                super.onBackPressed();
            }
        } else {
            Log.e("НАЖАЛИ НАЗАД ЕЛСЕ", String.valueOf(count));
            super.onBackPressed();
        }
    }
    public static boolean isOnlineNet(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}