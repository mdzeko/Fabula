package app.vz.hr.fabula.async_requests;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.vz.hr.fabula.R;

/**
 * Created by miso on 9/16/15
 */
public class GETRequest extends AsyncTask<String, String, String> {
    View loading;
    public GETRequest(){

    }
    public GETRequest(View loadingView){
        this.loading = loadingView;
    }

    @Override
    protected void onPreExecute() {
        if(loading == null)
            return;
        loading.setVisibility(View.VISIBLE);
        if(loading instanceof TextView)
            ((TextView) loading).setText(R.string.loading);

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0].replace("+", ""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader br;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            else
                br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
                sb.append(output);
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(loading == null)
            return;
        if(loading instanceof TextView)
            ((TextView) loading).setText(R.string.done);
    }
}
