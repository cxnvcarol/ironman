package cmua.ironman;

//import cmua.accelerometerplay.AccelerometerPlayActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final UUID standardSPPUUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP
	// UUID
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final String PROPULSORES_ACTIVADOS = "x";
	private static final String ENSAMBLAR_ARMADURA = "y";
	private static final int REQUEST_CAMERA = 4;
	private static final String DISPARAR = "z";

	// private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private BluetoothAdapter mBluetoothAdapter = null;
	// private ComponentName compName;
	private BluetoothDevice bluetoothDevice;
	private boolean mIsBluetoothConnected = false;
	private BluetoothSocket mBTSocket;
	private ReadInput mReadThread = null;
	private Button btnSendBth;
	private EditText mEditSend;
	private TextView mTxtReceive;
	private Button btnClearBth;

	private TextView txtVoice;

	@Override
	protected void onStart() {
		super.onStart();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			// TODO // if (mChatService == null) setupChat();
		}
	}

	@Override
	protected void onDestroy() {
		new DisConnectBT().execute();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
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
	//	case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
		//	serverIntent = new Intent(this, DeviceListActivity.class);
			//startActivityForResult(serverIntent,
				//	REQUEST_CONNECT_DEVICE_INSECURE);
			//return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	private void ensureDiscoverable() {
		System.out.println("Ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
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
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return HomeFragment.newInstance(position + 1);
				// case 1:
				// return BluetoothFragment.newInstance(position+1);
			default:
				return null; // Shouldn't happen.
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

	public void goToChat() {
		// finish();
		// Intent homepage = new Intent(this, BluetoothChat.class);
		// startActivity(homepage);
	}

	public void goToPlay() {
		// finish();
		// Intent homepage = new Intent(this, AccelerometerPlayActivity.class);
		// startActivity(homepage);
	}

	public void goToVoice() {
		finish();
		Intent homepage = new Intent(this, MainActivity.class);
		startActivity(homepage);
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {

		switch (reqCode) {
		case REQUEST_CONNECT_DEVICE_INSECURE:
		case REQUEST_CONNECT_DEVICE_SECURE:

			if (resultCode == RESULT_OK) {
				if (mBTSocket == null || !mIsBluetoothConnected) {
					bluetoothDevice = mBluetoothAdapter.getRemoteDevice(data
							.getExtras().getString(
									DeviceListActivity.EXTRA_DEVICE_ADDRESS));

					new ConnectBT().execute();
					enableViewObjs();
				}

			}
			break;

		case REQUEST_CAMERA:
			//TODO GET photo. Algo asï¿½:
			/*
			 *  
    	       try{
    	    	   super.onActivityResult(requestCode, resultCode, data);
    	       layoutImage.setVisibility(LinearLayout.VISIBLE);
    	       foto = (Bitmap) data.getExtras().get("data");
    	       imgDenuncia.setImageBitmap(foto);
    	       layoutImageButtons.setVisibility(LinearLayout.GONE);
    	       }
    	       catch(Exception e)
    	       {
    	    	   e.printStackTrace();
    	       }
			 */
			break;
		case HomeFragment.VOICE_RECOGNITION_REQUEST_CODE:
		default:
			try{
				if (resultCode == MainActivity.RESULT_OK) {
			
				if (txtVoice == null)
					txtVoice = (TextView) findViewById(R.id.txtVoice);
				recognizeVoiceCommand(data);
			}
			}catch(Exception e)
			{
				System.out.println("Recognition voice failed");
			}
			break;
		}
	}

	public void recognizeVoiceCommand(Intent data) throws IOException {
		String wordStr = "";
		ArrayList<String> matches = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		
		for (int i = 0; i < matches.size(); i++) {
			wordStr+=";"+matches.get(i);
		}
		txtVoice.setText(wordStr);
		String command=matches.get(0).toLowerCase();
		if(command.contains("hola"))
			msg("Hola Stark");
		else if(command.contains("propulsores"))
			activarPropulsores();
		else if(command.contains("armadura"))
			ensamblarArmadura();
		else if(command.contains("camara")||command.contains("cámara"))
			abrirCamara();
		else if(command.contains("dispara"))
			{
			this.disparar();
			
			}
		tryOpenApp(matches.get(0));

	}

	private void disparar() throws IOException {
		msg("Dispara!");
		sendBth(DISPARAR);
		HomeFragment.disparar();//TODO
		
	}

	private void abrirCamara() {
		msg("Abriendo cï¿½mara");
		try{
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        startActivityForResult(intent, REQUEST_CAMERA);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void ensamblarArmadura() throws IOException {
		msg("Ensamblando armadura");
		sendBth(ENSAMBLAR_ARMADURA);
	}
	private void activarPropulsores() throws IOException {
		msg("Propulsores activados");
		sendBth(PROPULSORES_ACTIVADOS);
				
	}

	private void sendBth(String msg) throws IOException {
		mBTSocket.getOutputStream().write(msg.getBytes());		
	}

	private void tryOpenApp(String string) {
		String[] words=string.split(" ");
		String firstWord = words[0];
		if (firstWord.equals("open")) {
			String secondWord = words[1];
			PackageManager packageManager = getPackageManager();
			List<PackageInfo> packs = packageManager.getInstalledPackages(0);
			int size = packs.size();
			for (int v = 0; v < size; v++) {
				PackageInfo p = packs.get(v);
				String tmpAppName = p.applicationInfo.loadLabel(packageManager)
						.toString();
				String pname = p.packageName;
				tmpAppName = tmpAppName.toLowerCase();
				if (tmpAppName.trim().toLowerCase()
						.equals(secondWord.trim().toLowerCase())) {
					PackageManager pm = getPackageManager();
					Intent appStartIntent = pm.getLaunchIntentForPackage(pname);
					System.out.println(pname);
					if (null != appStartIntent) {
						try {
							this.startActivity(appStartIntent);
						} catch (Exception e) {
						}
					}
				}
			}
		} 
	}

	private void enableViewObjs() {

		mTxtReceive = (TextView) findViewById(R.id.txtReceive);
		mTxtReceive.setMovementMethod(new ScrollingMovementMethod());
		mEditSend = (EditText) findViewById(R.id.editSend);
		btnSendBth = (Button) findViewById(R.id.btnSend);
		btnSendBth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// msg("Sending msg");
				try {
					mBTSocket.getOutputStream().write(
							mEditSend.getText().toString().getBytes());
					mEditSend.setText("");
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		});

		btnClearBth = (Button) findViewById(R.id.btnClear);
		btnClearBth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditSend.setText("");
				mTxtReceive.setText("");

			}
		});
		btnClearBth.setEnabled(true);

	}

	private void msg(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	private class ReadInput implements Runnable {

		protected static final int mMaxChars = 500;
		private boolean bStop = false;
		private Thread t;

		public ReadInput() {
			t = new Thread(this, "Input Thread");
			t.start();
		}

		public boolean isRunning() {
			return t.isAlive();
		}

		@Override
		public void run() {
			InputStream inputStream;

			try {
				inputStream = mBTSocket.getInputStream();
				while (!bStop) {
					byte[] buffer = new byte[256];
					if (inputStream.available() > 0) {
						inputStream.read(buffer);
						int i = 0;
						/*
						 * This is needed because new String(buffer) is taking
						 * the entire buffer i.e. 256 chars on Android 2.3.4
						 * http://stackoverflow.com/a/8843462/1287554
						 */
						for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
						}
						final String strInput = new String(buffer, 0, i);

						/*
						 * If checked then receive text, better design would
						 * probably be to stop thread if unchecked and free
						 * resources, but this is a quick fix
						 */
						mTxtReceive.post(new Runnable() {
							@Override
							public void run() {
								mTxtReceive.append(strInput);
								// Uncomment below for testing
								// mTxtReceive.append("\n");
								// mTxtReceive.append("Chars: " +
								// strInput.length() + " Lines: " +
								// mTxtReceive.getLineCount() + "\n");

								int txtLength = mTxtReceive.getEditableText()
										.length();
								if (txtLength > mMaxChars) {
									mTxtReceive.getEditableText().delete(0,
											txtLength - mMaxChars);
								}
							}
						});

					}
					Thread.sleep(500);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void stop() {
			bStop = true;
		}

	}

	private class DisConnectBT extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				if (mReadThread != null) {
					mReadThread.stop();
					while (mReadThread.isRunning())
						; // Wait until it stops
					mReadThread = null;

				}

			} catch (Exception e) {
				System.err.println("Capturada!!");
				e.printStackTrace();
			}
			try {
				if(mBTSocket!=null)
					mBTSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mIsBluetoothConnected = false;
			// if (mIsUserInitiatedDisconnect) {
			// finish();
			// }
		}

	}

	private class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean mConnectSuccessful = true;

		@Override
		protected void onPreExecute() {
			msg("Connecting");// http://stackoverflow.com/a/11130220/1287554
		}

		@Override
		protected Void doInBackground(Void... devices) {

			try {
				if (mBTSocket == null || !mIsBluetoothConnected) {
					mBTSocket = bluetoothDevice
							.createInsecureRfcommSocketToServiceRecord(standardSPPUUID);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					mBTSocket.connect();

				}
			} catch (IOException e) {
				// Unable to connect to device
				e.printStackTrace();
				mConnectSuccessful = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (!mConnectSuccessful) {
				btnSendBth.setEnabled(true);
				Toast.makeText(getApplicationContext(),
						"Could not connect to device. Is it a Serial device?",
						Toast.LENGTH_LONG).show();
				// finish();
			} else {
				msg("Connected to device");
				mIsBluetoothConnected = true;
				mReadThread = new ReadInput(); // Kick off input reader
			}

			// progressDialog.dismiss();
		}

	}

}
