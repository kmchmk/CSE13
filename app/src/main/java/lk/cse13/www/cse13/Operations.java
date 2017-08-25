package lk.cse13.www.cse13;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


class Operations {

    public static String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = MainActivity.mainContext.openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
        }

        return ret;
    }

    public static void writeToFile(String data, String file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.mainContext.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException ignored) {
        }
    }
}

