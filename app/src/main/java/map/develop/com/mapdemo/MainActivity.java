package map.develop.com.mapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import map.develop.com.mapdemo.adapter.RestaurantAdapter;
import map.develop.com.mapdemo.db.DatabaseHelper;
import map.develop.com.mapdemo.model.Restaurant;
import map.develop.com.mapdemo.utils.DataParser;
import map.develop.com.mapdemo.utils.DownloadUrl;
import map.develop.com.mapdemo.utils.Generic;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
    LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
  private GoogleMap mMap;
  private Location mLastLocation;
  private Marker mCurrLocationMarker;
  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private int PROXIMITY_RADIUS = 2000;
  private ArrayList<Restaurant> dataList = new ArrayList<>();
  private RestaurantAdapter restaurantAdapter;
  private RecyclerView recyclerView;
  private DatabaseHelper databaseHelper;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = findViewById(R.id.list);
    databaseHelper = new DatabaseHelper(this);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    if (!Generic.isNetworkAvailable(this)) {
      Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
      dataList = databaseHelper.getData();
      if (dataList != null && dataList.size() > 0) {
        setRecyclerDataToAdapter();
      }

    }
  }

  private void loadRestaurantData() {
    String Restaurant = "restaurant";
    String url = getUrl(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Restaurant);
    Object[] DataTransfer = new Object[2];
    DataTransfer[0] = mMap;
    DataTransfer[1] = url;
    Log.d("onClick", url);
    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
    getNearbyPlacesData.execute(DataTransfer);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
      } else {
        getLocationPermission();
      }
    } else {
      buildGoogleApiClient();
      mMap.setMyLocationEnabled(true);
    }

  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API).build();
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnected(Bundle bundle) {

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(1000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi
          .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onLocationChanged(Location location) {

    mLastLocation = location;
    if (mCurrLocationMarker != null) {
      mCurrLocationMarker.remove();
    }
    //Place current location marker
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title("Current Position");
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    mCurrLocationMarker = mMap.addMarker(markerOptions);

    //move map camera
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    //stop location updates
    if (mGoogleApiClient != null) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    if (Generic.isNetworkAvailable(MainActivity.this)) {
      loadRestaurantData();
    }


  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  private void getLocationPermission() {
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      buildGoogleApiClient();
      mMap.setMyLocationEnabled(true);
    } else {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String permissions[],
      @NonNull int[] grantResults) {

    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          buildGoogleApiClient();
          mMap.setMyLocationEnabled(true);
        }
      }
    }
  }

  private String getUrl(double latitude, double longitude, String nearbyPlace) {

    StringBuilder googlePlacesUrl = new StringBuilder(
        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
    googlePlacesUrl.append("location=" + latitude + "," + longitude);
    googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
    googlePlacesUrl.append("&type=" + nearbyPlace);
    googlePlacesUrl.append("&sensor=true");
    googlePlacesUrl.append("&key=" + "AIzaSyCq179894LXc7HlDo_Umnv9sXbgom6R0EY");
    Log.d("getUrl", googlePlacesUrl.toString());
    return (googlePlacesUrl.toString());
  }

  class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
      try {
        Log.d("GetNearbyPlacesData", "doInBackground entered");
        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        googlePlacesData = downloadUrl.readUrl(url);
        Log.d("GooglePlacesReadTask", "doInBackground Exit" + googlePlacesData.toString());
      } catch (Exception e) {
        Log.d("GooglePlacesReadTask", e.toString());
      }
      return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("GooglePlacesReadTask", "onPostExecute Entered");
      List<HashMap<String, String>> nearbyPlacesList = null;
      DataParser dataParser = new DataParser();
      nearbyPlacesList = dataParser.parse(result);
      ShowNearbyPlaces(nearbyPlacesList);
      Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
      databaseHelper.deleteData();
      dataList = new ArrayList<>();
      for (int i = 0; i < nearbyPlacesList.size(); i++) {
        Log.d("onPostExecute", "Entered into showing locations");
        HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
        String placeName = googlePlace.get("place_name");
        String vicinity = googlePlace.get("vicinity");
        String rating = googlePlace.get("rating");
        String icon = googlePlace.get("icon");
        String photo = googlePlace.get("photo_reference");
        if (!TextUtils.isEmpty(placeName)) {
          Restaurant restaurant = new Restaurant(placeName, vicinity, icon, rating);
          restaurant.setPhotoReference(photo);
          dataList.add(restaurant);
          databaseHelper.insertData(restaurant);
        }


      }

      setRecyclerDataToAdapter();
    }
  }

  private void setRecyclerDataToAdapter() {
    restaurantAdapter = new RestaurantAdapter(dataList, this);
    recyclerView.addItemDecoration(
        new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    recyclerView.setAdapter(restaurantAdapter);
  }
}
