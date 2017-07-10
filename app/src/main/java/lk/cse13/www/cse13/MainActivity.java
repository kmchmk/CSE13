package lk.cse13.www.cse13;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    public static WebView webview;
    public static String url = "www.cse13.lk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webview = (WebView) findViewById(R.id.webView);

        webview.setWebViewClient(new MyBrowser());
        webview.setWebChromeClient(new WebChromeClient() {
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
        });


        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        String index = getIntent().getStringExtra("index");
        String password = getIntent().getStringExtra("password");

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body onload='document.frm1.submit()'>\n" +
                "<form action='http://www.cse13.lk/ctrl/setSession.php' method='post' name='frm1'>\n" +
                "  <input type='hidden' name='index' value='"+index+"'><br>\n" +
                "  <input type='hidden' name='pw' value='"+password+"'><br>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>";
        MainActivity.webview.loadData(html, "text/html", "UTF-8");



new Updates().execute();

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(MainActivity.url)) {
                return false;
            } else {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        }

    }


    class Updates extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            int thisAppVesion = 0;//change this everytime updating the app
            String versionURL = "http://13.58.202.127/cse13android/version.php";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;

                response = httpclient.execute(new HttpGet(new URI(versionURL)));

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String responseString = out.toString();
                    out.close();

                    if (new JSONObject(responseString).getInt("newversion") > thisAppVesion) {
                        return responseString;
                    } else {
                        return null;
                    }
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    return null;
                }
            } catch (Exception e) {
                return "no_internet";
            }

        }

        @Override
        protected void onPostExecute(String message) {
            if (message != null) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    final String apkurl = jsonObject.getString("apkurl");
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                    dlgAlert.setMessage(jsonObject.getString("message"));
                    dlgAlert.setTitle(jsonObject.getString("title"));
                    dlgAlert.setPositiveButton(jsonObject.getString("positivebutton"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.webview.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(apkurl)));
                        }
                    });
                    dlgAlert.setNegativeButton(jsonObject.getString("negativebutton"), null);
                    dlgAlert.create().show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(message=="no_internet"){
                MainActivity.webview.loadUrl("about:blank");
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setTitle("No internet connection!");
                dlgAlert.setMessage("Please connect to the internet and restart the application");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                dlgAlert.create().show();
            }
        }
    }

}