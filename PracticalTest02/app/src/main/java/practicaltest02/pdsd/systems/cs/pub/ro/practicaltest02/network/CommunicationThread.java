package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.general.Constants;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.general.Utilities;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.model.UrlInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String url = bufferedReader.readLine();

            HashMap<String, UrlInformation> data = serverThread.getData();
            UrlInformation urlInformation = null;
            String resString = "";

            if (data.containsKey(url)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                urlInformation = data.get(url);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");

                HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
                HttpGet httpget = new HttpGet("http://" + url); // Set the action you want to do
                HttpResponse response = httpclient.execute(httpget); // Executeit
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent(); // Create an InputStream with the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) // Read line by line
                        sb.append(line).append("\n");

                resString = sb.toString(); // Result is here

                is.close(); // Close the stream

                urlInformation = new UrlInformation(resString);
                serverThread.setData(url, urlInformation);
            }

            String result = urlInformation.getUrl();

            Log.e(Constants.TAG, result);
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
