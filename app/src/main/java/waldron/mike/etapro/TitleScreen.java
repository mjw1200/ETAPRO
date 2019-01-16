package waldron.mike.etapro;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
                    // Test to see whether location services are enabled. If yes, indicate success. If no, an ApiException is thrown.
                    task.getResult(ApiException.class);
                    indicateSuccess();
                }
                catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Pop a dialog that asks the user to enable location services. Results show up in onActivityResult(...)
                                ((ResolvableApiException) exception).startResolutionForResult(activity, Constants.LOCATION_SERVICES_CHECK);
                            } catch (Exception e) { e.printStackTrace(); }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // This app can't function without location permissions. If we can't even ask for permission, exit.
                            finishAndRemoveTask();
                            break;
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
                // This app can't function without location permissions. If the user said no, exit.
                finishAndRemoveTask();
            }
            else {
                indicateSuccess();
            }
        }
    }

    /**
     * Pop a toast to indicate success. This method can go away once the app is doing something useful.
     */
    private void indicateSuccess() {
        Toast t = Toast.makeText(this, "Green light", Toast.LENGTH_SHORT);
        t.show();
    }
}
