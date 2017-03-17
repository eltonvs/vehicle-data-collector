package br.ufrn.imd.vehicle_data_collector.io;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LogJSONWriter {
    private static final String TAG = LogJSONWriter.class.getName();
    private boolean isFirstLine;
    private BufferedWriter buf;

    public LogJSONWriter(String filename, String dirname) throws FileNotFoundException, RuntimeException {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + File.separator + dirname);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Log.d(TAG, "Path is " + sdCard.getAbsolutePath() + File.separator + dirname);

            File file = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            this.buf = new BufferedWriter(osw);
            this.isFirstLine = true;

            Log.d(TAG, "Constructed the LogJSONWriter");
        } catch (Exception e) {
            Log.e(TAG, "LogJSONWriter constructor failed");
        }
    }

    public void closeLogJSONWriter() {
        try {
            buf.flush();
            buf.close();

            Log.d(TAG, "Flushed and closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
