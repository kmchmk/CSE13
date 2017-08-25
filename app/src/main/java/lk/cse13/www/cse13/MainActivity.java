package lk.cse13.www.cse13;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.http.util.EncodingUtils;


public class MainActivity extends AppCompatActivity {
    private WebView webview;
    public static Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = getApplicationContext();
        FloatingActionButton settingsfb = (FloatingActionButton) findViewById(R.id.settingsfb);
        settingsfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new MyBrowser());
        webview.setWebChromeClient(new MyChromeBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
//        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        login();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        login();
    }

    private void login() {
        String index = Operations.readFromFile("ind");
        String password = Operations.readFromFile("psd");
        String postData = "index=" + index + "&pw=" + password;
        webview.postUrl("http://www.cse13.lk/ctrl/setSession.php", EncodingUtils.getBytes(postData, "BASE64"));
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals("http://www.cse13.lk/signin.php")) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                Bundle b = new Bundle();
                b.putString("msg", "none");
                i.putExtras(b);
                startActivity(i);
            } else if (url.equals("http://www.cse13.lk/signin.php?msg=error")) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                Bundle b = new Bundle();
                b.putString("msg", "error");
                i.putExtras(b);
                startActivity(i);
            }

            if (Uri.parse(url).getHost().equals("www.cse13.lk")) {
                return false;
            } else {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            webview.loadUrl("about:blank");
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setTitle("No internet connection!");
            dlgAlert.setMessage("Connect to the internet and try again!");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dlgAlert.create().show();
        }
    }


    private class MyChromeBrowser extends WebChromeClient {
        private ProgressDialog mProgress;

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.show();
            }
            mProgress.setMessage("Loading");
            if (progress == 100) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }
}

