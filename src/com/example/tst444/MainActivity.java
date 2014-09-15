package com.example.tst444;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	BluetoothAdapter mBluetoothAdapter;
	int REQUEST_ENABLE_BT = 7;
	TextView resultTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resultTextView = (TextView)findViewById(R.id.textViewResult);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}else {
			onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, null);
		}
		
		((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listOfDevices = "";
				resultTextView.setText(listOfDevices);				
			}
		});
		
		((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent discoverableIntent = new
						Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
						discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0 /*3600*/);
						startActivity(discoverableIntent);				
			}
		});
		
		
	}
	
	String listOfDevices = "";
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            update(device.getName()/* + "\n" + device.getAddress()*/);
	        }
	    }
	};
	
	Handler mHandler;
	Runnable discoverer = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mBluetoothAdapter.startDiscovery()) {
				//update("Started discovery!");
			} else {
				update("Problem starting the discovery!");
			}
			mHandler.postDelayed(this, 5000);
		}
	};
	
	void update(String s) {
		listOfDevices += s + "\n";
        resultTextView.setText(listOfDevices);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT) {
			if(resultCode == RESULT_OK) {
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, filter); 
				
				mHandler = new Handler();
				discoverer.run();
			} else {
				update("Bluetooth not enabled.");
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
