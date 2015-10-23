package com.example.esp.esp8266;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class HomeActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button buttonPin11,buttonPin12,buttonPin13;
    private SeekBar seekbar;
    private TextView text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonPin11 = (Button)findViewById(R.id.buttonPin11);
        buttonPin12 = (Button)findViewById(R.id.buttonPin12);
        buttonPin13 = (Button)findViewById(R.id.buttonPin13);
        seekbar = (SeekBar)findViewById(R.id.seekBarspeed);
        text_view = (TextView)findViewById(R.id.textViewmy);
        text_view.setText("Speed: " +  seekbar.getProgress() + "/" + seekbar.getMax());

        buttonPin11.setOnClickListener(this);
        buttonPin12.setOnClickListener(this);
        buttonPin13.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View view) {

        String parameterValue = "";
        int parameterValue2 = 0;
        String ipAddress = "192.168.43.152";
        String portNumber = "80";

        if(view.getId()==buttonPin11.getId())
        {
            parameterValue = "11";
            parameterValue2 = seekbar.getProgress();
        }
        else if(view.getId()==buttonPin12.getId())
        {
            parameterValue = "12";
            parameterValue2 = seekbar.getProgress();
        }
        else if (view.getId()==buttonPin13.getId())
        {
            parameterValue = "13";
            parameterValue2 = seekbar.getProgress();
        }

        if(ipAddress.length()>0 && portNumber.length()>0) {
            new HttpRequestAsyncTask(
                    parameterValue, parameterValue2, ipAddress, portNumber, "pin", "val"
            ).execute();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        text_view.setText("Speed: " +  progress + "/" + seekbar.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        private String ipAddress, portNumber;
        private String parameter, parameter2;
        private String parameterValue;
        private int parameterValue2;

        public HttpRequestAsyncTask(String parameterValue, int parameterValue2, String ipAddress, String portNumber, String parameter, String parameter2)
        {
            this.ipAddress = ipAddress;
            this.parameterValue = parameterValue;
            this.parameterValue2 = parameterValue2;
            this.portNumber = portNumber;
            this.parameter = parameter;
            this.parameter2 = parameter2;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendRequest(parameterValue,parameterValue2,ipAddress,portNumber, parameter, parameter2);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected void onPreExecute() {
        }

    }

    public void sendRequest(String parameterValue,int parameterValue2, String ipAddress, String portNumber, String parameterName,String parameterName2) {

        try {
            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client

            URI website = new URI("http://"+ipAddress+":"+portNumber+"/?"+parameterName+"="+parameterValue+"/?"+parameterName2+"="+parameterValue2);   // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request

            HttpResponse response = httpclient.execute(getRequest); // execute the request     // get the ip address server's reply

            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            content.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
