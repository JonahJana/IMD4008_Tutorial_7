package name.tutorial1.tutorial7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textCurrentSteps;
    private int stepCount;

    private Intent serviceIntent;

    private DataUpdateReceiver dataUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // number of steps
        textCurrentSteps = findViewById(R.id.cSNum);

        // step counter service
        serviceIntent = new Intent(this, StepCounterService.class);
    }

    public void startPedometerClick(View v){
        clearSteps();

        stopService(serviceIntent);
        if(Build.VERSION.SDK_INT < 26){
            startService(serviceIntent);
        }else{
            startForegroundService(serviceIntent);
        }
    }

    public void stopPedometerClick(View v){
        stopService(serviceIntent);

        clearSteps();
    }

    private void clearSteps(){
        stepCount = 0;
        textCurrentSteps.setText(Integer.toString(stepCount));
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            stepCount = intent.getIntExtra(StepCounterService.STEP_COUNT, 0);
            textCurrentSteps.setText(Integer.toString(stepCount));
        }
    }

    @Override
    protected void onResume(){
        IntentFilter intentFilter = new IntentFilter(StepCounterService.REFRESH_DATA_INTENT);
        dataUpdateReceiver = new DataUpdateReceiver();
        registerReceiver(dataUpdateReceiver, intentFilter);

        Intent intent = new Intent(StepCounterService.REQUEST_DATA_INTENT);
        sendBroadcast(intent);

        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(dataUpdateReceiver);
        super.onPause();
    }
}
