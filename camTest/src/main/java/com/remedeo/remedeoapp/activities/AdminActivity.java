package com.remedeo.remedeoapp.activities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.helper.PatientHelper;

public class AdminActivity extends Activity {
	private Button btnExport;
	private static final String EXPORTFILE = "export.json";
	private static final String FOLDER_MAIN = "Remidio";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		
		btnExport = (Button) findViewById(R.id.admin_btnExport);
		
		btnExport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exportFiles();
				
			}
		});
	}
	
	private void exportFiles() {
		new ExportFileAsync().execute();
	}
	
	private class ExportFileAsync extends AsyncTask<Void, Void, Void> {
		
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(AdminActivity.this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("Exporting FIle");
			dialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			List<PatientEntity> patientList = PatientHelper.fetchPatients(
					getApplicationContext(), null, null, null, null, null);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + FOLDER_MAIN);
			try {
				if (file.exists() && patientList != null
						&& !patientList.isEmpty()) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							file.getAbsolutePath() + File.separator
									+ EXPORTFILE));
					writer.write(new Gson().toJson(patientList));
					writer.close();
				}
			}
			catch (IOException e) {
				Log.e("IO", "IOException", e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	
}
