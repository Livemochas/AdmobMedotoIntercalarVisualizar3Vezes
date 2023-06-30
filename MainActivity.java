
package com.base;

import static android.webkit.WebView.RENDERER_PRIORITY_BOUND;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //variavel de numeros interios criados
    int carregamento = 0;
    private WebView myWebView;
    private static final String TAG = "MyActivity";
    private InterstitialAd interstitialAd;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // funçao para iniciar o sdk do admob
        MobileAds.initialize(this, initializationStatus -> {
        });
        loadAd();

        // funçao principal do app para visualizaçao de paginas da web----------------------------------------------
        myWebView = findViewById(R.id.webview);
        myWebView.loadUrl("file:///android_asset/website.html");
        myWebView.requestFocus();

        // javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setUserAgentString(WebSettings.getDefaultUserAgent(this));
        String newUA= "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
        myWebView.getSettings().setUserAgentString(newUA);

        //API Renderer Importance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { myWebView.setRendererPriorityPolicy(RENDERER_PRIORITY_BOUND, true); }

        // calsse de melhoramento para lincks downloads e urls amigaveis
        myWebView.setWebChromeClient(new WebChromeClient() {public boolean onConsoleMessage(ConsoleMessage cm) { Log.d("MyApplication", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId() );return true; }});
        myWebView.setWebViewClient(new CustomWebViewClient());
        myWebView.setDownloadListener(new CustomDownloadListener());

    }


    //funçao do app para retorna a paginas pelo botao do android------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();return true; }return super.onKeyDown(keyCode, event); }

    class CustomWebViewClient extends WebViewClient
    {
        //funçao para contar carregamento de paginas
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            VezesProcessada();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        // funçao de implementaçao do webview
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Objects.equals(Uri.parse(url).getHost(), "learn.livemochas.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "www0.livemochas.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "m.livemochas.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "www.facebook.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "web.facebook.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "facebook.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "m.facebook.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "accounts.google.com")) { return false; }
            if (Objects.equals(Uri.parse(url).getHost(), "google.com")) { return false; }
            else {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

        }
        //funçao do app para mandar um pagina de rro pessonalizada
        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            myWebView.loadUrl("file:///android_asset/erro.html");
        }

    }
    // funçao do app para monitorar os downloads que se faz no webview------------------------------
    class CustomDownloadListener implements DownloadListener {
        public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(getString(R.string.download));
            builder.setMessage(getString(R.string.question));
            builder.setCancelable(false).setPositiveButton((R.string.ok), (dialog, id) -> {
                //download file using web browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }).setNegativeButton((R.string.cancel), (dialog, id) -> dialog.cancel());

            builder.create().show();

        }

    }
    // codigo para abir anuncios a cada 5 carregamentos de pagina
    private void VezesProcessada() {

        // incremente 1 a cada carregamento
        carregamento += 1;
        // funcao para  abrir pagina se carregar mais de 5 vezes
        if (carregamento >= 5) {
            //aqui abre a pagina depois da contagem
            //Toast.makeText(this, "visualizou mais de 3 vezes!", Toast.LENGTH_SHORT).show();

            showInterstitial();
            // aqui zera o cronometro
            carregamento = 0;
        }
    }
    public void loadAd() {
        //Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
        AdRequest adRequest = new AdRequest.Builder().build();
        String AD_UNIT_ID = getString(R.string.app_inst_id);
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        loadAd();

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                        loadAd();

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                        //showInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;


                        @SuppressLint("DefaultLocale")
                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        //Toast.makeText(MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }

}
