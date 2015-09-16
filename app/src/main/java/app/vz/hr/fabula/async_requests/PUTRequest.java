package app.vz.hr.fabula.async_requests;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.vz.hr.fabula.util.DBUtil;
import app.vz.hr.fabula.util.JSONParse;

/**
 * Created by miso on 9/16/15
 */
public class PUTRequest extends AsyncTask<String, String, String> {
    URL url;
    public PUTRequest(String url){
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            this.url = null;
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (this.url == null)
            return null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", DBUtil.addAuthentication());
            String r;
            try {
                r = getResponse(connection);
            }catch (IOException e1){
                e1.printStackTrace();
                r = getResponse(connection);
            }
            if ((JSONParse.responseOK(r) || JSONParse.DBExists(r)) && params.length > 1 && !params[0].isEmpty() && !params[1].isEmpty()) {
                URL url2 = new URL(url.toString() + "/" + params[1]);
                HttpURLConnection connection1 = (HttpURLConnection) url2.openConnection();
                connection1.setRequestMethod("PUT");
                connection1.setDoOutput(true);
                connection1.setRequestProperty("Content-Type", "application/json");
                connection1.setRequestProperty("Authorization", DBUtil.addAuthentication());
                OutputStreamWriter dataOutputStream = new OutputStreamWriter(connection1.getOutputStream());
                //String encoded = URLEncoder.encode(params[0], "UTF-8");
                dataOutputStream.write(params[0]);
                dataOutputStream.close();
                return getResponse(connection1);
            }
            else
                return getResponse(connection);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    private String getResponse(HttpURLConnection connection) throws IOException{
        BufferedReader br;
        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_ACCEPTED || code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED)
            br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

        else
            br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
        StringBuilder sb = new StringBuilder();
        String output;

        while ((output = br.readLine()) != null)
            sb.append(output);
        return sb.toString();
    }
}
