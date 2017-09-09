package www.cse13.lk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String msg = b.getString("msg");
            assert msg != null;
            if (msg.equals("error")) {
                TextView errorMsg = (TextView) findViewById(R.id.errorMsg);
                errorMsg.setText(R.string.try_again);
            }
        }
        indexbox.setText(Operations.readFromFile("ind"));
        passwordbox.setText(Operations.readFromFile("psd"));

        saveLoginButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String index = indexbox.getText().toString();
                        String password = passwordbox.getText().toString();
                        Operations.writeToFile(index, "ind");
                        Operations.writeToFile(password, "psd");
                        finish();
                    }
                });

        clearAll.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        indexbox.setText("");
                        passwordbox.setText("");
                        Operations.writeToFile("", "ind");
                        Operations.writeToFile("", "psd");
                    }
                });
    }

}

