package com.remedeo.remedeoapp.adpater;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.activities.GridViewActivity;
import com.remedeo.remedeoapp.entities.PatientEntity;

public class DirectoryListAdapter extends BaseAdapter {
	
	public interface DeletePatient
	{
		void deletePatient(int position);
	}

	private Activity activity;
	private List<PatientEntity> patientList;
	private LayoutInflater infalter;
	private static final String PATIENT_OBJECT = "PATIENT_OBJECT";

	public DirectoryListAdapter(Activity activity,
			List<PatientEntity> patientList) {
		this.activity = activity;
		this.patientList = patientList;
		infalter = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return patientList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = infalter
					.inflate(R.layout.directory_item_layout, null);
		}

		RelativeLayout layout = (RelativeLayout) convertView
				.findViewById(R.id.dir_layout);
		TextView directoryNameTextView = (TextView) convertView
				.findViewById(R.id.diectory_item_textview);

		final PatientEntity patient = patientList.get(position);

		if (patient != null) {
			final String fileName = patient.getPatientId() + "_"
					+ patient.getName() + "_" + patient.getDOB();

			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Remidio/" + fileName);

			if (!file.exists()) {
				file = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Remidio/" + "0"
						+ patient.getPatientId() + "_" + patient.getName()
						+ "_" + patient.getDOB());
			}

			if (file.exists() && file.isDirectory()) {

				directoryNameTextView.setText(patient.getPatientId() + "\n"
						+ patient.getName());

				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent in = new Intent(activity, GridViewActivity.class);
						in.putExtra("directoryPath", fileName);
						in.putExtra(PATIENT_OBJECT, patient);
						in.putExtra("showAll", false);
						activity.startActivity(in);
					}
				});

				layout.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						((DeletePatient) activity).deletePatient(position);
						return false;
					}
				});
			}
		}
		return convertView;
	}

	

}
