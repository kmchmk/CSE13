package lk.cse13.www.cse13;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static android.R.string.cancel;
import static android.provider.Telephony.Mms.Part.FILENAME;

public class SettingsActivity extends AppCompatActivity {

    private EditText indexbox;
    private EditText passwordbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        indexbox = (EditText) findViewById(R.id.indexbox);
        passwordbox = (EditText) findViewById(R.id.passwordbox);
        Button saveLoginButton = (Button) findViewById(R.id.save_and_login_button);
        Button clearAll = (Button) findViewById(R.id.clear_all);


        indexbox.setText(readFromFile("ind"));
        passwordbox.setText(readFromFile("psd"));


        saveLoginButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String index = indexbox.getText().toString();
                        String password = passwordbox.getText().toString();
                        writeToFile(index, "ind");
                        writeToFile(password, "psd");
                        finish();
//                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(i);
                    }
                });

        clearAll.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        indexbox.setText("");
                        passwordbox.setText("");
                        writeToFile("", "ind");
                        writeToFile("", "psd");
                    }
                });
    }


    private void writeToFile(String data, String file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
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

