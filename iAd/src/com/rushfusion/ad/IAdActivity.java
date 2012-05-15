package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		final Button start1 = (Button) findViewById(R.id.button1);
		final Button start2 = (Button) findViewById(R.id.button2);
		final Button start3 = (Button) findViewById(R.id.button3);
		final Button start4 = (Button) findViewById(R.id.button4);

		final Button stop1 = (Button) findViewById(R.id.button5);
		final Button stop2 = (Button) findViewById(R.id.button6);
		final Button stop3 = (Button) findViewById(R.id.button7);
		final Button stop4 = (Button) findViewById(R.id.button8);

		start1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start1.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final AdCreator creator1 = new AdCreator(
								IAdActivity.this, "", new AdCreator.CallBack() {

									@Override
									public void onError(Exception e,
											int errorCode) {
										// TODO Auto-generated method stub
										System.out.println(e.getMessage());
									}
								});
						creator1.TEST_XML = "data1.xml";
						creator1.setAdSize(300, 300);
						creator1.start();
						stop1.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								creator1.stop();
							}
						});
					}
				});
			}
		});
		start2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start2.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final AdCreator creator2 = new AdCreator(
								IAdActivity.this, "", new AdCreator.CallBack() {

									@Override
									public void onError(Exception e,
											int errorCode) {
										// TODO Auto-generated method stub
										System.out.println(e.getMessage());
									}
								});
						creator2.TEST_XML = "data2.xml";
						creator2.setAdSize(300, 300);
						creator2.start();
						stop2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								creator2.stop();
							}
						});
					}
				});
			}
		});
		start3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start3.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						final AdCreator creator3 = new AdCreator(
								IAdActivity.this, "", new AdCreator.CallBack() {

									@Override
									public void onError(Exception e,
											int errorCode) {
										// TODO Auto-generated method stub
										System.out.println(e.getMessage());
									}
								});
						creator3.TEST_XML = "data3.xml";
						creator3.setAdSize(300, 300);
						creator3.start();
						stop3.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								creator3.stop();
							}
						});
					}
				});
			}
		});
		start4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start4.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final AdCreator creator4 = new AdCreator(
								IAdActivity.this, "", new AdCreator.CallBack() {

									@Override
									public void onError(Exception e,
											int errorCode) {
										// TODO Auto-generated method stub
										System.out.println(e.getMessage());
									}
								});
						creator4.TEST_XML = "data4.xml";
						creator4.setAdSize(300, 300);
						creator4.start();

						stop4.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								creator4.stop();
							}
						});
					}
				});
			}
		});

	}

}