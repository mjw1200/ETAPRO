package waldron.mike.etapro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class TitleScreen extends AppCompatActivity {

    /**
     * The activity constructor, kinda.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_screen);

        // hide the bar with the app name at the top
        getSupportActionBar().hide();
        enableLocation();
    }

    /**
     * Ensure that location services are enabled, and we have permission to use them. This is a requirement to run ETAPRO; if the app
     * can't use location services, it can't run.
     */
    private void enableLocation() {
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        final Activity activity = this;
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create().setPriority(PRIORITY_HIGH_ACCURACY));
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    // Test to see whether location services are enabled. If yes, check permissions. If no, an ApiException is thrown.
                    task.getResult(ApiException.class);
                    getLocationPermission();
                }
                catch (ApiException exception) {
                    final int statusCode = exception.getStatusCode();

                    if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            // Pop a dialog that asks the user to enable location services. Results show up in onActivityResult(...)
                            ((ResolvableApiException) exception).startResolutionForResult(activity, Constants.LOCATION_SERVICES_CHECK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        // This app can't function without location permissions. If we can't even ask for permission, exit.
                        finishAndRemoveTask();
                    }
                }
            }
        });
    }

    /**
     * Check the result of a startResolutionForResult(...) call
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOCATION_SERVICES_CHECK) {
            if (resultCode == RESULT_CANCELED) {
                // This app can't function without location enabled. If the user said no, exit.
                finishAndRemoveTask();
            }
            else {
                // Location is enabled. The app still needs permission to use it, though.
                getLocationPermission();
            }
        }
    }

    /**
     * Request location permission, if it hasn't already been granted
     */
    private void getLocationPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // The app doesn't have permission yet, so request it. The results of this call are available in onRequestPermissionsResult(...)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION);
        }
        else {
            // Permission has already been granted. Press on.
            nextActivity();
        }
    }

    /**
     * Check the result of a requestPermissions(...) call
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!(requestCode == Constants.LOCATION_PERMISSION && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // Location permissions are kind of critical. If they're not granted, we're out of here.
            finishAndRemoveTask();
        }
        else {
            // Permission was granted. Press on.
            nextActivity();
        }
    }

    /**
     * Start the DestinationScreen activity
     */
    private void nextActivity() {
        final Activity activity = this;
        Timer t = new Timer();

        // Start the next activity after a short delay (in ms) so the user can get a good look at the title screen
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, DestinationScreen.class);
                startActivity(intent);
            }
        }, 2250);
    }
}
