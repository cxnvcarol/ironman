package cmua.ironman;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {
	
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	private Button btnEscuchar;

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

	private TextView txtVoice;

	private Button btnDisparar;

	//private LocationClient mLocationClient;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
//    	mLocationClient = new LocationClient(this, this, this);
//    	 mLocationClient.connect();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        btnEscuchar=(Button)rootView.findViewById(R.id.btnEscuchar);
        btnDisparar=(Button)rootView.findViewById(R.id.btnDisparar);
        txtVoice=(TextView)rootView.findViewById(R.id.txtVoice);
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
    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        //startActivityForResult(intent, 0);
        System.out.println("hasta aqui todobien");
        
        startActivityForResult(intent, 0);
	}

    public void disparar()
    {
    	Camera cam = Camera.open();     
    	Parameters p = cam.getParameters();
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
    

    
    public void onActivityResult2(int requestCode, int resultCode, Intent data) {
    	System.out.println("Ahora si se fue: "+requestCode+"-"+resultCode);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        String wordStr = null;
        String[] words = null;
        String firstWord = null;
        String secondWord = null;
        //if (requestCode == VOICE_RECOGNITION_REQUEST_CODE&&
                //&& 
                if(resultCode == MainActivity.RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            wordStr = matches.get(0);
            txtVoice.setText(wordStr);
            words = wordStr.split(" ");
            firstWord = words[0];
            secondWord = words[1];
            
            if (firstWord.equals("open")) {
                PackageManager packageManager = getActivity().getPackageManager();
                List<PackageInfo> packs = packageManager
                        .getInstalledPackages(0);
                int size = packs.size();
                for (int v = 0; v < size; v++) {
                    PackageInfo p = packs.get(v);
                    String tmpAppName = p.applicationInfo.loadLabel(
                            packageManager).toString();
                    String pname = p.packageName;
                    tmpAppName = tmpAppName.toLowerCase();
                    if (tmpAppName.trim().toLowerCase().
                            equals(secondWord.trim().toLowerCase())) {
                        PackageManager pm = getActivity().getPackageManager();
                        Intent appStartIntent = pm.getLaunchIntentForPackage(pname);
                        if (null != appStartIntent) {
                            try {                            
                                this.startActivity(appStartIntent);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
              } // end of open app code	
        }
    }
   
    public void changeTab(int pos)
    {
    	((ActionBarActivity)getActivity()).getSupportActionBar().setSelectedNavigationItem(pos);
    }

}

