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

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;

import com.onesignal.OneSignal;

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
    String urGogoDef = "https://go.5mincredit.com.ua/WY7SbYg2";
    String urGogo = "";
    String deepPartone = "";
    String namingSl = "";
    String afStatus = "";
    SharedPreferences.Editor editor;
    // end.
    private static final String AF_DEV_KEY = "qLBVL3yAsrHaY8PgyhfLBW";
    private static final String ONESIGNAL_APP_ID = "d2eddeaa-e298-472b-8d9f-7129298da1b0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                String c = "campaign";
                String as = "af_status";
                for (String attrName : conversionData.keySet()) {

                    if (c.equals(attrName)) {
                        namingSl = (String) conversionData.get(attrName);

                        Log.e("mylog:appflyer_IFFF_one", "nameC ="+ namingSl);
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
                        deepPartone ="?"+ dParts[1];
                       // deepPartone =  deepPart.replace("{UID}", appsFlyerId);

                        Log.d("mylog:appflyer000", "deeplink part=" + deepPartone);
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
            Log.e("mylog:URLсохран", "urGogo=" + urGogo+"---APP_PREFERENCES_URL="+APP_PREFERENCES_URL);
        } else {
            urGogo = urGogoDef;
            Log.e("mylog:URLсохранELSE", "urGogo=" + urGogo+"---APP_PREFERENCES_URL="+APP_PREFERENCES_URL);
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
            FacebookSdk.setAutoInitEnabled(true);
            FacebookSdk.fullyInitialize();
            AppLinkData.fetchDeferredAppLinkData(this,
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            if (appLinkData != null) {
                                final String facelink = appLinkData.getTargetUri().getQuery(); //берем диплинк из фейсбук
                                Log.i("DEBUG_FACEBO_SDK_mylog", "DeepLink facelink:" + facelink);
                                urlhost = "?" + facelink;
                                Log.i("DEBUG_FACEBO_SDK_mylog", "DeepLink urlhost:" + urlhost);
                                try {
                                    Vnutr();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i("DEBUG_FACEBO_SDK_mylog", "AppLinkData is Null");
                                try {
                                    Vnutr();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            );

        } else {
            intent = new Intent(this, noinet.class);
            startActivity(intent);
        }
    }
    public void Vnutr () throws InterruptedException {
        int timer_count = 0;
        while (timer_count < 10 && afStatus.isEmpty()) {
            Thread.sleep(1000);
            timer_count++;
            Log.e("mylog_naming:", "ПАУЗА:"+timer_count+ "--namingComp=" + namingSl+"--afStatus="+ afStatus);
        }
        Log.e("mylog_naming:", "START2 операций с дип и нейминг namingComp=" + namingSl);

        if (urGogo.equals(urGogoDef) == true ) {
            if (!deepPartone.isEmpty()) {
                urGogo = urGogo.concat(deepPartone);
                Log.e("mylog_deep:", "urGogo:"+urGogo);
            } else {
                if (!namingSl.isEmpty() && !namingSl.equals("None")) {
                    Log.e("mylog_naming:", "eeeeee1111");
                    String[] nameParam = {"utm_creative","utm_campaign","id","ad_id","adset_name"};
                    try {
                        String[] nParts = namingSl.split("_");

                        Log.e("mylog_naming:", "eeeeee" + nParts[0] + " & " + nParts[0]);
                        String namingPart = "";

                        for (int i = 0; i < nParts.length; i++) {
                            if (i == 0) {
                                namingPart = namingPart.concat("?"+nameParam[i] + "=" + nParts[i]);
                            } else {
                                // namingPart = namingPart.concat("&");

                                namingPart = namingPart.concat("&"+nameParam[i] + "=" + nParts[i]);
                            }
                            Log.e("mylog_naming_FOR:", "namingPart= " + namingPart);
                        }
                        urGogo = urGogo.concat(namingPart);
                        Log.e("mylog_naming:", "urlGo:" + urGogo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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