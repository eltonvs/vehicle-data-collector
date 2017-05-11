package br.ufrn.imd.vdc.io;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.vdc.R;


public class ObdAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = ObdAsyncTask.class.getName();
    private Activity context;
    private BluetoothDevice device;
    private BluetoothSocket btSocket;
    private TextView tvResults;
    private List<ObdCommand> readings = new ArrayList<>();

    public ObdAsyncTask(Activity context, BluetoothDevice device) {
        super();
        this.context = context;
        this.device = device;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "ObdTask - onPreExecute...");
        tvResults = (TextView) context.findViewById(R.id.tvResults);
        tvResults.setText(context.getString(R.string.loading));

        // Creating socket
        try {
            btSocket = BluetoothManager.connect(device);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when starting a bluetooth connection", e);
            return;
        }

        if (!btSocket.isConnected()) {
            Log.e(TAG, "Bluetooth Socket isn't connected");
            cancel(true);
            return;
        }
        Log.d(TAG, "Bluetooth Socket connected");
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.i(TAG, "ObdTask - doInBackground...");

        obdSetup();

        try {
            InputStream socketIS = btSocket.getInputStream();
            OutputStream socketOS = btSocket.getOutputStream();

            for (ObdCommand cmd : ObdCommandList.getInstance().getCommands()) {
                cmd.run(socketIS, socketOS);
                readings.add(cmd);
            }
        } catch (Exception e) {
            Log.e(TAG, "Some error occurred", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(TAG, "ObdTask - onPostExecute...");

        try {
            btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth Socket", e);
        }

        // Write result on TextView
        StringBuilder sb = new StringBuilder();
        for (ObdCommand cmd : readings) {
            sb.append(cmd.getName()).append(" = ").append(cmd.getFormattedResult()).append("\n");
        }
        if (!sb.toString().isEmpty()) {
            tvResults.setText(sb.toString());
        }
    }

    private void obdSetup() {
        try {
            new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new TimeoutCommand(125).run(btSocket.getInputStream(), btSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (Exception e) {
            Log.e(TAG, "Some error occurred", e);
        }
    }
}
