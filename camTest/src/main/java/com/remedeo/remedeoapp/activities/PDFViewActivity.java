package com.remedeo.remedeoapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.remedeo.remedeoapp.R;

public class PDFViewActivity extends Activity {
	
	private Button backButton;
	private Button printButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdfview);
		
		
		backButton=(Button)findViewById(R.id.pdfview_back_button);
		printButton=(Button)findViewById(R.id.pdfview_print_button);
	}
	
}
