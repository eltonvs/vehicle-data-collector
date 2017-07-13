package br.ufrn.imd.vdc.activities;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.pires.obd.enums.ObdProtocols;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.vdc.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    // Data Sync
    public static final String DATA_SYNC_SWITCH = "data_sync_switch";
    public static final String DATA_SYNC_POST_URL = "post_url";
    public static final String DATA_SYNC_VEHICLE_ID = "vehicle_id";
    public static final String DATA_SYNC_FREQUENCY = "sync_frequency";
    // Bluetooth
    public static final String BLUETOOTH_SWITCH = "bluetooth_switch";
    public static final String BLUETOOTH_DEVICES = "bluetooth_devices";
    // GPS
    public static final String GPS_SWITCH = "gps_switch";
    // OBD
    public static final String OBD_PROTOCOL = "obd_protocol";
    public static final String ENGINE_DISPLACEMENT = "engine_displacement";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener
        sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment {
        private final String TAG = SettingsFragment.class.getName();

        // Bluetooth
        private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        private ListPreference btDevices;

        // Data/Sync
        SwitchPreference dataSyncSwitch;
        EditTextPreference uploadURL;
        EditTextPreference vehicleID;
        ListPreference syncFrequency;

        // GPS
        private LocationManager locationManager;
        private SwitchPreference gpsSwitch;

        // OBD
        private ListPreference obdProtocols;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate: Creating Fragment...");
            addPreferencesFromResource(R.xml.preferences);

            // Bluetooth
            SwitchPreference btSwitch = (SwitchPreference) findPreference(BLUETOOTH_SWITCH);
            btDevices = (ListPreference) findPreference(BLUETOOTH_DEVICES);

            if (btAdapter == null) {
                // Bluetooth not available
                btSwitch.setChecked(false);
                btSwitch.setEnabled(false);
                btDevices.setEnabled(false);
                Toast
                    .makeText(getActivity(), "This device does not support Bluetooth.", Toast
                        .LENGTH_LONG)
                    .show();

                // Terminate
                return;
            }

            // Set switch with the current bluetooth status
            btSwitch.setChecked(btAdapter.isEnabled());

            if (btAdapter.isEnabled()) {
                fillBluetoothDevicesList();
            }

            btSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = !((SwitchPreference) preference).isChecked();

                    if (val) {
                        turnOnBluetooth();
                    } else {
                        turnOffBluetooth();
                    }

                    return true;
                }
            });
            bindPreferenceSummaryToValue(btDevices);


            // Data/Sync
            dataSyncSwitch = (SwitchPreference) findPreference(DATA_SYNC_SWITCH);
            uploadURL = (EditTextPreference) findPreference(DATA_SYNC_POST_URL);
            vehicleID = (EditTextPreference) findPreference(DATA_SYNC_VEHICLE_ID);
            syncFrequency = (ListPreference) findPreference(DATA_SYNC_FREQUENCY);

            dataSyncSwitch.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean val = !((SwitchPreference) preference).isChecked();

                        if (val) {
                            turnOnDataSync();
                        } else {
                            turnOffDataSync();
                        }

                        return true;
                    }
                });

            bindPreferenceSummaryToValue(uploadURL);
            bindPreferenceSummaryToValue(vehicleID);
            bindPreferenceSummaryToValue(syncFrequency);


            // GPS
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            gpsSwitch = (SwitchPreference) findPreference(GPS_SWITCH);

            if (locationManager == null) {
                // Bluetooth not available
                gpsSwitch.setChecked(false);
                gpsSwitch.setEnabled(false);
                Toast
                    .makeText(getActivity(), "This device does not support GPS.", Toast
                        .LENGTH_LONG)
                    .show();

                // Terminate
                return;
            }

            // Set switch with the current GPS status
            gpsSwitch.setChecked(isGPSEnabled());

            gpsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = !((SwitchPreference) preference).isChecked();

                    if (val) {
                        turnOnGPS();
                    } else {
                        turnOffGPS();
                    }

                    return true;
                }
            });


            // OBD
            obdProtocols = (ListPreference) findPreference(OBD_PROTOCOL);
            fillObdProtocols();
            bindPreferenceSummaryToValue(obdProtocols);

            EditTextPreference engineDisplacement = (EditTextPreference) findPreference
                (ENGINE_DISPLACEMENT);
            bindPreferenceSummaryToValue(engineDisplacement);
        }

        private void fillBluetoothDevicesList() {
            if (btAdapter != null) {
                ArrayList<CharSequence> pairedDevicesStrings = new ArrayList<>();
                ArrayList<CharSequence> pairedDevicesAddress = new ArrayList<>();
                for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                    pairedDevicesStrings.add(device.getName() + "\n" + device.getAddress());
                    pairedDevicesAddress.add(device.getAddress());
                }

                btDevices.setEntries(pairedDevicesStrings.toArray(new CharSequence[0]));
                btDevices.setEntryValues(pairedDevicesAddress.toArray(new CharSequence[0]));
            }
        }

        private void turnOnBluetooth() {
            if (btAdapter != null) {
                btAdapter.enable();
                btDevices.setEnabled(true);
            }
        }

        private void turnOffBluetooth() {
            if (btAdapter != null) {
                btAdapter.disable();
                btDevices.setEnabled(false);
            }
        }

        /**
         * TODO: Create Data Sync module (turn on)
         */
        private void turnOnDataSync() {
            throw new UnsupportedOperationException();
        }

        /**
         * TODO: Create Data Sync Module (turn off)
         */
        private void turnOffDataSync() {
            throw new UnsupportedOperationException();
        }

        private void turnOnGPS() {
            Toast.makeText(getActivity(), "Turning GPS on... (not working yet)", Toast.LENGTH_SHORT)
                .show();
        }

        private void turnOffGPS() {
            Toast
                .makeText(getActivity(), "Turning GPS off... (not working yet)", Toast
                    .LENGTH_SHORT)
                .show();
        }

        private boolean isGPSEnabled() {
            return locationManager != null &&
                   locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        private void fillObdProtocols() {
            ArrayList<CharSequence> protocolsStrings = new ArrayList<>();
            for (ObdProtocols protocol : ObdProtocols.values()) {
                protocolsStrings.add(protocol.name());
            }
            obdProtocols.setEntries(protocolsStrings.toArray(new CharSequence[0]));
            obdProtocols.setEntryValues(protocolsStrings.toArray(new CharSequence[0]));
        }
    }

}
