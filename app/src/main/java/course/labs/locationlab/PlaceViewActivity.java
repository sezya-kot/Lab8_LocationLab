package course.labs.locationlab;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PlaceViewActivity extends ListActivity implements LocationListener {
	private static final long FIVE_MINS = 5 * 60 * 1000;
	private static final String TAG = "Lab-Location";

	// False if you don't have network access
	public static boolean sHasNetwork = false;

	private Location mLastLocationReading;
	private PlaceViewAdapter mAdapter;
	private LocationManager mLocationManager;
	private boolean mMockLocationOn = false;

	// default minimum time between new readings
	private long mMinTime = 5000;

	// default minimum distance between old and new readings.
	private float mMinDistance = 1000.0f;

	// A fake location provider used for testing
	private MockLocationProvider mMockLocationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the app's user interface. This class is a ListActivity,
        // so it has its own ListView. ListView's adapter should be a PlaceViewAdapter

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ListView placesListView = getListView();

		// TODO - add a footerView to the ListView
		// You can use footer_view.xml to define the footer

		getListView().setFooterDividersEnabled(true);
		TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view, null);

		// TODO - footerView must respond to user clicks, handling 3 cases:

		// There is no current location - response is up to you. The best
		// solution is to always disable the footerView until you have a
		// location.

		// There is a current location, but the user has already acquired a
		// PlaceBadge for this location - issue a Toast message with the text -
		// "You already have this location badge." 
		// Use the PlaceRecord class' intersects() method to determine whether 
		// a PlaceBadge already exists for a given location

		// There is a current location for which the user does not already have
		// a PlaceBadge. In this case download the information needed to make a new
		// PlaceBadge.

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			clickedFooter();

			}

		});

		placesListView.addFooterView(footerView);
		mAdapter = new PlaceViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);

	}

	private void clickedFooter() {

		// footerView must respond to user clicks.
		// Must handle 3 cases:
		// 1) The current location is new - download new Place Badge. Issue the
		// following log call:
		// log("Starting Place Download");
		if (mLastLocationReading != null) {
			if (isNewLocation(mLastLocationReading)) {
				PlaceDownloaderTask downloader = new PlaceDownloaderTask(PlaceViewActivity.this, false);
				downloader.execute(mLastLocationReading);
				Toast.makeText(getApplicationContext(), "Checking new place data", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Starting Place Download");
			}

			// 2) The current location has been seen before - issue Toast
			// message.
			// Issue the following log call:
			// log("You already have this location badge");
			else {
				Log.d(TAG, "You already have this location badge");
				Toast.makeText(getApplicationContext(), "You already have this location badge", Toast.LENGTH_SHORT).show();
			}
		}

		// 3) There is no current location - response is up to you. The best
		// solution is to disable the footerView until you have a location.
		// Issue the following log call:
		// log("Location data is not available");
		else {
			Log.d(TAG, "Location data is not available");
		}
	}

	private boolean isNewLocation(Location newLocation) {
		List<PlaceRecord> places = mAdapter.getList();
		for(PlaceRecord place : places){
			Log.d("MINE", "Old location: " + place.getLocation().toString());
			Log.d("MINE", "New location: " + newLocation.toString());
			if(newLocation.distanceTo(place.getLocation()) < mMinDistance){
				Log.d("MINE", "Not new");
				return false;
			}
			Log.d("MINE", "New");
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		startMockLocationManager();

		// TODO - Check NETWORK_PROVIDER for an existing location reading.
		// Only keep this last reading if it is fresh - less than 5 minutes old

		mLastLocationReading = getLastKnownLocation();

		// TODO - register to receive location updates from NETWORK_PROVIDER
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);

	}

	private Location getLastKnownLocation() {
		List<String> matchingProviders = mLocationManager.getAllProviders();

		for (String provider : matchingProviders) {

			// Note: Uncomment these lines for coursera tests
			//if (!provider.equals(LocationManager.NETWORK_PROVIDER))
			//	continue;

			Location location = mLocationManager.getLastKnownLocation(provider);

			if (location != null) {
				if (ageInMilliseconds(location) < FIVE_MINS) {
					Log.d(TAG, "The choosen provider was: " + provider);
					return location;
				}
			}
		}
		return null;
	}

	@Override
	protected void onPause() {

		// TODO - unregister for location updates
		mLocationManager.removeUpdates(this);
        
        
		shutdownMockLocationManager();
		super.onPause();
	}

	// Callback method used by PlaceDownloaderTask
	public void addNewPlace(PlaceRecord place) {
	
		// TODO - Attempt to add place to the adapter, considering the following cases
		mAdapter.add(place);

		// A PlaceBadge for this location already exists - issue a Toast message
		// with the text - "You already have this location badge." Use the PlaceRecord 
		// class' intersects() method to determine whether a PlaceBadge already exists
		// for a given location. Do not add the PlaceBadge to the adapter
		
		// The place is null - issue a Toast message with the text
		// "PlaceBadge could not be acquired"
		// Do not add the PlaceBadge to the adapter
		
		// The place has no country name - issue a Toast message
		// with the text - "There is no country at this location". 
		// Do not add the PlaceBadge to the adapter
		
		// Otherwise - add the PlaceBadge to the adapter
		

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
	}

	// LocationListener methods
	@Override
	public void onLocationChanged(Location currentLocation) {

		// TODO - Update location considering the following cases.
		// 1) If there is no last location, set the last location to the current
		// location.
		if (mLastLocationReading == null) {
			mLastLocationReading = currentLocation;
		}
		// 2) If the current location is older than the last location, ignore
		// the current location
		else if (currentLocation.getTime() < mLastLocationReading.getTime())
			return;
		// 3) If the current location is newer than the last locations, keep the
		// current location.

		else {
			mLastLocationReading = currentLocation;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// not implemented
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not implemented
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not implemented
	}

	// Returns age of location in milliseconds
	private long ageInMilliseconds(Location location) {
		return System.currentTimeMillis() - location.getTime();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_badges:
			mAdapter.removeAllViews();
			return true;
		case R.id.place_one:
			mMockLocationProvider.pushLocation(37.422, -122.084);
			return true;
		case R.id.place_no_country:
			mMockLocationProvider.pushLocation(0, 0);
			return true;
		case R.id.place_two:
			mMockLocationProvider.pushLocation(38.996667, -76.9275);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void shutdownMockLocationManager() {
		if (mMockLocationOn) {
			mMockLocationProvider.shutdown();
		}
	}

	private void startMockLocationManager() {
		if (!mMockLocationOn) {
			mMockLocationProvider = new MockLocationProvider(
					LocationManager.NETWORK_PROVIDER, this);
		}
	}
}
