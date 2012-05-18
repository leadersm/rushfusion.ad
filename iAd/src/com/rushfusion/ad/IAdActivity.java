package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	
	boolean isShowing=false;
	AdCreator creator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		
		final Button start1 = (Button) findViewById(R.id.button1);
		
		start1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isShowing){
					isShowing = true;
					start1.post(r1);
				}else{
					isShowing = false;
					creator.stop();
				}
			}
		});
}
	
	
	Runnable r1 = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator = AdCreator.getInstance(IAdActivity.this, "", new AdCreator.CallBack() {
						
						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator.TEST_XML = "data.xml";
//			creator1.setAdSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			creator.start();
		}
	};

}
