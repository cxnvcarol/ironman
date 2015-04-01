package cmua.ironman;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.media.AudioManager;
/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	public static final int VOICE_RECOGNITION_REQUEST_CODE = 123;

	private ComponentName componentMediaButton;

	private Button btnEscuchar;
	private Button btnSendBth;
	private Button btnClearBth;

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static HomeFragment newInstance(int sectionNumber) {
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	private View rootView;



	private Button btnDisparar;

	//private LocationClient mLocationClient;

	public HomeFragment() {
	}


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		//((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(compName);

		super.onStop();
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("on start!");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		enableMediaButton();



		System.out.println("on createView!");
		//    	mLocationClient = new LocationClient(this, this, this);
		//    	 mLocationClient.connect();
		rootView = inflater.inflate(R.layout.fragment_main, container, false);
		btnEscuchar=(Button)rootView.findViewById(R.id.btnEscuchar);
		btnDisparar=(Button)rootView.findViewById(R.id.btnDisparar);
		btnSendBth=(Button)rootView.findViewById(R.id.btnSend);
		btnClearBth=(Button)rootView.findViewById(R.id.btnClear);
		// disableBluetoothButtons();

		btnEscuchar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();
			}
		});

		btnDisparar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				disparar();
			}
		});
		return rootView;
	}
	private void disableBluetoothButtons() {
		btnSendBth.setEnabled(false);
		btnClearBth.setEnabled(false);
	}


	public void startVoiceRecognitionActivity() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-Latn");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-Latn");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		//startActivityForResult(intent, 0);
		System.out.println("hasta aqui todobien");

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	public static void disparar()
	{
		Camera cam = Camera.open();     

		Parameters p = cam.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_OFF);
		cam.setParameters(p);
		cam.startPreview();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		cam.setParameters(p);
		cam.startPreview();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cam.stopPreview();
		cam.release();

	}

	void enableMediaButton()
	{
		componentMediaButton=new ComponentName(
				getActivity().getPackageName(), 
				MediaButtonIntentReceiver.class.getName());
		((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).registerMediaButtonEventReceiver(
				componentMediaButton);
	}

	@Override
	public void onResume() {
		Bundle casitodo = getActivity().getIntent().getExtras();
		boolean mediaButton=casitodo!=null&&casitodo.getBoolean("mediaButton");

		if(mediaButton)
		{

			mediaButton=false;
			getActivity().getIntent().removeExtra("mediaButton");
			Toast.makeText(getActivity(), "Jarvis disponible", Toast.LENGTH_LONG).show();
			startVoiceRecognitionActivity();    			
		}
		enableMediaButton();
		//else
		//Toast.makeText(getActivity(), "Pailas", Toast.LENGTH_LONG).show();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		System.out.println("ondestroyed!");
		((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(componentMediaButton);

		super.onDestroy();
	}

	public void changeTab(int pos)
	{
		((ActionBarActivity)getActivity()).getSupportActionBar().setSelectedNavigationItem(pos);
	}

}

