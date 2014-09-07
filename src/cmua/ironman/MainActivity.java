package cmua.ironman;


import cmua.accelerometerplay.AccelerometerPlayActivity;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    //private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    private BluetoothAdapter mBluetoothAdapter = null;
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
           //TODO // if (mChatService == null) setupChat();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
    	getMenuInflater().inflate(R.menu.option_menu, menu);    	
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
       // return super.onOptionsItemSelected(item);
    	
    	 Intent serverIntent = null;
         switch (item.getItemId()) {
         case R.id.secure_connect_scan:
             // Launch the DeviceListActivity to see devices and do scan
             serverIntent = new Intent(this, DeviceListActivity.class);
             startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
             return true;
         case R.id.insecure_connect_scan:
             // Launch the DeviceListActivity to see devices and do scan
             serverIntent = new Intent(this, DeviceListActivity.class);
             startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
             return true;
         case R.id.discoverable:
             // Ensure this device is discoverable by others
             ensureDiscoverable();
             return true;
         }
         return false;
    }
    

    private void ensureDiscoverable() {
        System.out.println("Ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
        	switch(position)
        	{
        	case 0:
        		return HomeFragment.newInstance(position + 1);
        	case 1:
        		return BluetoothFragment.newInstance(position+1);
        	default:
        		return null; //Shouldn't happen.
        	}
            
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Bluetooth chat";
                case 2:
                    return "Motion sensor";
            }
            return null;
        }
    }
    
    public void goToChat()
    {
    	//finish();
		//Intent homepage = new Intent(this, BluetoothChat.class);
		//startActivity(homepage);
    }
    public void goToPlay()
    {
    	finish();
		Intent homepage = new Intent(this, AccelerometerPlayActivity.class);
		startActivity(homepage);
    }
    public void goToVoice()
    {
    	finish();
		Intent homepage = new Intent(this, MainActivity.class);
		startActivity(homepage);
    }
    

    /**
     * A placeholder fragment containing a simple view.
     */
    
}
