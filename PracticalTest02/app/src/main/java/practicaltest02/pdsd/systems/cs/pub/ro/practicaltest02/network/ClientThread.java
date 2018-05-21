package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.general.Constants;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private String url;
    private int port;
    private TextView urlInformationTextView;

    private Socket socket;

    public ClientThread(String address, int port, String url, TextView urlInformationTextView) {
        this.address = address;
        this.port = port;
        this.url = url;
        this.urlInformationTextView = urlInformationTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(url);
            printWriter.flush();


            String urlInformation;
            while ((urlInformation = bufferedReader.readLine()) != null) {
                final String finalizedUrlInformation = urlInformation;

                urlInformationTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        urlInformationTextView.setText(finalizedUrlInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
