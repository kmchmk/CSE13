package lk.cse13.www.cse13;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    public static WebView webview;
    public static String url = "www.cse13.lk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        login();
        new Updates().execute();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        login();
    }

    private void login() {
        String index = readFromFile("ind");
        String password = readFromFile("psd");
        String postData = "index=" + index + "&pw=" + password;
        webview.postUrl("http://www.cse13.lk/ctrl/setSession.php", EncodingUtils.getBytes(postData, "BASE64"));
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


    class Updates extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            int thisAppVesion = 1;//change this everytime updating the app
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

            if (message == "no_internet") {
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

    private String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
//            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
//            Log.e("Exception", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
