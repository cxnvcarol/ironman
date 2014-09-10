package cmua.ironman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

public class MediaButtonIntentReceiver extends BroadcastReceiver {

public MediaButtonIntentReceiver() {
    super();
}

@Override
public void onReceive(Context context, Intent intent) {
	System.out.println("buttmed");
    String intentAction = intent.getAction();
    System.out.println("intAc: "+intentAction);
    if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        return;
    }
    KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
    if (event == null) {
        return;
    }
    int action = event.getAction();
    System.out.println("evAc: "+action);
    if (action == KeyEvent.ACTION_DOWN) {
    // do something
    	
        Toast.makeText(context, "BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("mediaButton", true);        
        context.startActivity(intent);
        //context.
    }
    abortBroadcast();
}
}