package auto.ausiot.autosensor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;

import java.util.List;

import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.schedule.User;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.vo.Schedule;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterUser extends AppCompatActivity {

     // UI references.

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private EditText mAddresssView;
    private EditText mFullNameView;

    PlacesClient placesClient;

    LatLng mLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


         // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    registerUser();
                    return true;
                }
                return false;
            }
        });

        mAddresssView = (EditText) findViewById(R.id.address);
        mFullNameView = (EditText) findViewById(R.id.name);
        mAddresssView.setEnabled(false);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               registerUser();
            }
        });



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        initPlaces();
        addSearchFragment();



    }

    public void onClick(View v) {

    }
    void initPlaces() {
        Context ctx = getApplicationContext();
        if (!Places.isInitialized()) {

            String gApiKey = ctx.getString(R.string.api_key);
            Places.initialize(ctx, gApiKey);
        }
        //placesClient = Places.createClient(ctx);
    }

    void addSearchFragment() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
               // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                mAddresssView.setText(place.getAddress() );
                mLatlng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
               // Log.i(TAG, "An error occurred: " + status);
            }
        });
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        //startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

//    void searchPlaces(String place){
//         AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//        //RectangularBounds bounds = RectangularBounds.newInstance(first, sec);
//        FindAutocompletePredictionsRequest request =
//                FindAutocompletePredictionsRequest.builder()
//                        //.setLocationBias(bounds)
//                        .setCountry("au")
//                        .setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(place)
//                        .build();
//
//        placesClient.findAutocompletePredictions(request)
//                .addOnSuccessListener(
//                        new OnSuccessListener<FindAutocompletePredictionsResponse>() {
//                            @Override
//                            public void onSuccess(FindAutocompletePredictionsResponse response) {
//                                int x = response.getAutocompletePredictions().size();
//                                StringBuilder sb = new StringBuilder();
//                                for (AutocompletePrediction prediction :
//                                        response.getAutocompletePredictions()) {
//                                    sb.append(prediction.getPrimaryText(null).toString());
//                                    sb.append("\n");
//                                }
//                                mAddresssView.setText(sb.toString());
//                            }
//                        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        //exception.printStackTrace();
//                    }
//                });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                //Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }
//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }
//
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }
//


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void registerUser() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        RestCallBack rcallback =  new RestCallBack() {
            @Override
            public void onResponse(Schedule scvo) {

            }

            @Override
            public void onResponse(String token, String user) {

                finish();
            }

            @Override
            public void onFailure() {
                ;
            }

        };
        User user = new User();
        user.setEmail(mEmailView.getText().toString());
        user.setPassword(mPasswordView.getText().toString());
        user.setFullname(mFullNameView.getText().toString());
        user.setAddress(mAddresssView.getText().toString());
        user.setLatitude(mLatlng.latitude);
        user.setLongitude(mLatlng.longitude);
        ScheduleHelper sh = new ScheduleHelper();
        sh.register(user,null,rcallback);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
    }
}

