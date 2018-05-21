package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.R;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.general.Constants;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.network.ClientThread;
import practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private Button clientGetURLButton = null;
    private TextView clientUrlTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private GetUrlButtonClickListener getUrlButtonClickListener = new GetUrlButtonClickListener();
    private class GetUrlButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();

            if (clientAddress.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            clientUrlTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread("localhost", Integer.parseInt(serverPortEditText.getText().toString()),
                    clientAddress, clientUrlTextView
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);


        clientAddressEditText = findViewById(R.id.client_url_edit_text);
        clientGetURLButton = findViewById(R.id.client_get_button);
        clientGetURLButton.setOnClickListener(getUrlButtonClickListener);
        clientUrlTextView = findViewById(R.id.client_url_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
