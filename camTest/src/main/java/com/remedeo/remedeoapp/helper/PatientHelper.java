package com.remedeo.remedeoapp.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.remedeo.remedeoapp.common.DatabaseHandler;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.exceptions.DuplicateRecordException;

public class PatientHelper {

	private static final String TABLE_NAME = "PATIENT_INFO";

	public static final String KEY_ID = "_ID";
	public static final String KEY_PATIENT_ID = "PATIENT_ID";
	public static final String KEY_NAME = "NAME";
	public static final String KEY_GENDER = "GENDER";
	public static final String KEY_AGE = "AGE";
	public static final String KEY_DOB = "DOB";
	public static final String KEY_DOCTOR = "DOCTOR";
	public static final String KEY_HOSPITAL_NAME = "HOSPITAL_NAME";
	public static final String KEY_COMMENT = "COMMENT";
	public static final String KEY_ADDRESS = "ADDRESS";
	

	private static final String TAG = "PatientHelper";

	/**
	 * Method to insert patient id
	 * 
	 * @param db2
	 * 
	 * @param context
	 * @param patient
	 * @throws DuplicateRecordException
	 */
	public static void insertPatient(SQLiteDatabase database, Context context,
			PatientEntity patient) {
		SQLiteDatabase db;
		if (database == null) {
			DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
			db = dbHandler.getWritableDatabase();
		} else {
			db = database;
		}
		try {
			db.insert(TABLE_NAME, null, getCV(patient));
		} catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException", e);
		} finally {
			if (database == null && db != null) {
				db.close();
			}
		}
	}

	// get content values
	private static ContentValues getCV(PatientEntity patient) {
		ContentValues cv = new ContentValues();

		cv.put(KEY_PATIENT_ID, patient.getPatientId());
		cv.put(KEY_NAME, patient.getName());
		cv.put(KEY_AGE, patient.getAge());
		cv.put(KEY_COMMENT, patient.getComment());
		cv.put(KEY_ADDRESS, patient.getAddress());
		cv.put(KEY_DOB, patient.getDOB());
		cv.put(KEY_GENDER, patient.getGender());
		cv.put(KEY_DOCTOR, patient.getDoctorName());
		cv.put(KEY_ADDRESS, patient.getAddress());

		return cv;
	}

	/**
	 * Getting patients
	 * 
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public static List<PatientEntity> fetchPatients(Context context,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		List<PatientEntity> patients = new ArrayList<PatientEntity>();
		DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
		SQLiteDatabase db = dbHandler.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_NAME, null, selection,
					selectionArgs, groupBy, having, orderBy);
			while (cursor.moveToNext()) {
				patients.add(getPatientFromCursor(cursor));
			}
			cursor.close();
		} catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException", e);
		} catch (CursorIndexOutOfBoundsException e) {
			Log.e(TAG, "CursorIndexOutOfBoundsException", e);
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException", e);
		} finally {
		}
		return patients;
	}

	// /**
	// * Getting patients
	// *
	// * @param context
	// * @param selection
	// * @param selectionArgs
	// * @param groupBy
	// * @param having
	// * @param orderBy
	// * @return
	// */
	// public static List<PatientEntity> fetchOldPatients(Context context,
	// String selection, String[] selectionArgs, String groupBy,
	// String having, String orderBy) {
	// List<PatientEntity> patients = new ArrayList<PatientEntity>();
	// DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
	// SQLiteDatabase db = dbHandler.getReadableDatabase();
	// try {
	// Cursor cursor = db.query("PATIENT_INFO", null, selection,
	// selectionArgs, groupBy, having, orderBy);
	// while (cursor.moveToNext()) {
	// patients.add(getPatientFromCursor(cursor));
	// }
	// cursor.close();
	// }
	// catch (SQLiteException e) {
	// Log.e(TAG, "SQLiteException", e);
	// }
	// catch (CursorIndexOutOfBoundsException e) {
	// Log.e(TAG, "CursorIndexOutOfBoundsException", e);
	// }
	// catch (IllegalStateException e) {
	// Log.e(TAG, "IllegalStateException", e);
	// }
	// finally {
	// }
	// return patients;
	// }

	private static PatientEntity getPatientFromCursor(Cursor cursor) {
		PatientEntity patient = new PatientEntity();

		patient.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
		patient.setPatientId(cursor.getString(cursor
				.getColumnIndex(KEY_PATIENT_ID)));
		patient.setAge(cursor.getInt(cursor.getColumnIndex(KEY_AGE)));
		patient.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
		patient.setDOB(cursor.getString(cursor.getColumnIndex(KEY_DOB)));
		patient.setDoctorName(cursor.getString(cursor
				.getColumnIndex(KEY_DOCTOR)));
		patient.setGender(cursor.getString(cursor.getColumnIndex(KEY_GENDER)));
		patient.setHospitalName(cursor.getString(cursor
				.getColumnIndex(KEY_HOSPITAL_NAME)));
		patient.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		patient.setAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
		return patient;
	}

	public static void insertPatient(Context context, List<PatientEntity> list) {
		DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
		SQLiteDatabase db = dbHandler.getWritableDatabase();
		try {
			for (PatientEntity patient : list) {
				insertPatient(db, context, patient);
			}
		} catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException", e);
		} finally {
			db.close();
		}

	}

	public static void deletePatient(Context context, String patientId) {
		DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
		SQLiteDatabase db = dbHandler.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, KEY_PATIENT_ID + "=?",
					new String[] { patientId });
		} catch (SQLException e) {
			Log.e(TAG, "SQLiteException", e);
		}
	}

	public static void updatePatient(Context context, PatientEntity patient) {
		DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
		SQLiteDatabase db = dbHandler.getWritableDatabase();

		try {
			db.update(TABLE_NAME, getCV(patient), KEY_PATIENT_ID + "=?",
					new String[] { patient.getPatientId() });
		} catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException", e);
		} finally {
			db.close();
		}
	}

}
