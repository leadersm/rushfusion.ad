package com.rushfusion.ad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Choose extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Intent intent = new Intent(Choose.this,IAdActivity.class);
		Button btn1= (Button) findViewById(R.id.button1);
		Button btn2= (Button) findViewById(R.id.button2);
		Button btn3= (Button) findViewById(R.id.button3);
		Button btn4= (Button) findViewById(R.id.button4);
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("kind", 1);
				startActivity(intent);
			}
		});
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("kind", 2);
				startActivity(intent);
			}
		});
		btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("kind", 3);
				startActivity(intent);
			}
		});
		btn4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("kind", 4);
				startActivity(intent);
			}
		});
		
		
	}
}
