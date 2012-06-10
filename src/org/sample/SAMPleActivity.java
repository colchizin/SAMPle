package org.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SAMPleActivity extends Activity implements OnClickListener{
    
	private Button goButton;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);        
        
        goButton=(Button) findViewById(R.id.button_go);
        goButton.setOnClickListener(this);
    }

	public void onClick(View arg0) {
		
		//Starte Schritterfassung
		//Warte 10 Sekunden
		
		//Wechsle zur LaufActivity
		startActivity(new Intent(this, WalkActivity.class));		
	}
}