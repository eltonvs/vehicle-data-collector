package br.ufrn.imd.vdc.helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothManager {
    private static final BluetoothManager instance = new BluetoothManager();
    private static final String TAG = BluetoothManager.class.getName();
    /*
     * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     * #createRfcommSocketToServiceRecord(java.util.UUID)
     *
     * "Hint: If you are connecting to a Bluetooth serial board then try using the
     * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
     * are connecting to an Android peer then please generate your own unique
     * UUID."
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;

    private BluetoothManager() {
    }

    public static BluetoothManager getInstance() {
        return instance;
    }

    /**
     * Instantiates a BluetoothSocket for the remote device and connects it.
     * <p/>
     * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might
     * -closed-bluetooth-on-android-4-3/18786701#18786701
     *
     * @return The BluetoothSocket
     * @throws IOException Bluetooth connection error
     */
    public BluetoothSocket connect() throws IOException {
        if (isConnected()) {
            if (socket.getRemoteDevice().getName().equals(device.getName())) {
                return socket;
            } else {
                disconnect();
            }
        }

        if (device == null) {
            Log.e(TAG, "connect: Device is not set");
            return null;
        }

        Log.d(TAG, "Starting Bluetooth connection..");
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
        } catch (IOException e) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..",
                e);
            fallbackConnect();
        }
        return socket;
    }

    private void fallbackConnect() throws IOException {
        if (socket == null) return;
        Class<?> clazz = socket.getRemoteDevice().getClass();
        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
        try {
            Method m = clazz.getMethod("createRfcommSocket", paramTypes);
            Object[] params = new Object[]{Integer.valueOf(1)};
            BluetoothSocket sockFallback = (BluetoothSocket) m.invoke(socket.getRemoteDevice(),
                params);
            sockFallback.connect();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e);
            throw new IOException(e.getMessage());
        }
    }

    public void disconnect() {
        if (socket.isConnected()) {
            try {
                socket.close();
                Log.d(TAG, "disconnect: Socket successfully disconnected");
            } catch (IOException e) {
                Log.e(TAG, "disconnect: Fail to close Bluetooth socket connection", e);
            }
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean setUpDevice(String macAddress) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.d(TAG, "setUpDevice: Creating a Bluetooth Device");
        device = btAdapter.getRemoteDevice(macAddress);
        Log.d(TAG, "setUpDevice: Device Name: " + device.getName());

        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d(TAG, "setUpDevice: Device is bonded, starting connection");
            return true;
        }

        Log.d(TAG, "setUpDevice: Error bonding device");
        return false;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}
