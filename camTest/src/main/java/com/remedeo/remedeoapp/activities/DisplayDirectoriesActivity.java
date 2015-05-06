package com.remedeo.remedeoapp.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.adpater.DirectoryListAdapter;
import com.remedeo.remedeoapp.adpater.DirectoryListAdapter.DeletePatient;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.helper.PatientHelper;

public class DisplayDirectoriesActivity extends Activity implements
		DeletePatient {

	private GridView direcotryGridView;
	private EditText searchEdtiText;
	private Button searchButton;
	private TextView noResultTextView;
	private List<PatientEntity> patientList = new ArrayList<PatientEntity>();
	private DirectoryListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_directories);

		direcotryGridView = (GridView) findViewById(R.id.directory_gridview);

		searchEdtiText = (EditText) findViewById(R.id.gridview_search_edittext);
		searchButton = (Button) findViewById(R.id.gridview_search_butotn);
		noResultTextView = (TextView) findViewById(R.id.search_no_result_textview);

		new GetDirectoryAsyncTask().execute();

	}

	private class GetDirectoryAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// List<File> direcotyList = GetImageFiles.getDirectoryOnly();

			patientList = PatientHelper.fetchPatients(getApplicationContext(),
					null, null, null, null, null);

			// Iterator<File> fileIterator = direcotyList.iterator();
			// while (fileIterator.hasNext()) {
			// File file = fileIterator.next();
			// if (file.exists()) {
			// if (!file.isDirectory()) {
			// fileIterator.remove();
			// }
			// }
			// }
			//
			// for (File file : direcotyList) {
			// patientList.add(file.getName());
			// }

			return null;
		}

		@Override
		public void onPostExecute(Void param) {
			if (patientList != null && !patientList.isEmpty()) {
				adapter = new DirectoryListAdapter(
						DisplayDirectoriesActivity.this, patientList);
				direcotryGridView.setAdapter(adapter);

				searchButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						String searchString = searchEdtiText.getText()
								.toString();
						if (searchString != null && !searchString.isEmpty()) {
							List<PatientEntity> searchPatients = new ArrayList<PatientEntity>();
							for (PatientEntity patient : patientList) {
								if (patient.getName().contains(searchString)) {
									searchPatients.add(patient);
								}
							}
							if (!searchPatients.isEmpty()) {
								noResultTextView.setVisibility(View.GONE);
								direcotryGridView.setVisibility(View.VISIBLE);

								direcotryGridView
										.setAdapter(new DirectoryListAdapter(
												DisplayDirectoriesActivity.this,
												searchPatients));
							} else {
								noResultTextView.setText(Html
										.fromHtml(getString(
												R.string.no_search_result)
												.replace(
														"*",
														"<b>" + searchString
																+ "</b>")));
								noResultTextView.setVisibility(View.VISIBLE);
								direcotryGridView.setVisibility(View.GONE);
							}
						} else {
							noResultTextView.setVisibility(View.GONE);
							direcotryGridView.setVisibility(View.VISIBLE);
							direcotryGridView
									.setAdapter(new DirectoryListAdapter(
											DisplayDirectoriesActivity.this,
											patientList));
						}

					}
				});
			}

		}
	}

	@Override
	public void deletePatient(final int position) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(String.format(
				getString(R.string.error_delete), patientList.get(position)
						.getName()));

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						PatientHelper.deletePatient(
								DisplayDirectoriesActivity.this, patientList
										.get(position).getPatientId());
						deleteFolder(patientList.get(position));

						int count = 0;
						Iterator<PatientEntity> iterator = patientList
								.iterator();
						while (iterator.hasNext()) {
							PatientEntity patient = iterator.next();
							if (count == position) {
								iterator.remove();
							}
							count++;
							
						}
						adapter = new DirectoryListAdapter(
								DisplayDirectoriesActivity.this, patientList);
						direcotryGridView.setAdapter(adapter);
					}

				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();

	}

	private void deleteFolder(PatientEntity patientObject) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "Remidio/" + patientObject.getPatientId()
				+ "_" + patientObject.getName() + "_" + patientObject.getDOB());

		if (file.exists()) {
			for (File f : file.listFiles()) {
				if (f.exists())
					f.delete();
			}
			file.delete();
		}
	}

}
