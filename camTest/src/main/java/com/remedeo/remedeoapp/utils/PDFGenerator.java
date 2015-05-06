package com.remedeo.remedeoapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.remedeo.remedeoapp.entities.PatientEntity;

public class PDFGenerator {
	
	private static final String TAG = "PDFGenerator";
	
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			Font.BOLD, BaseColor.GREEN);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
			Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 10,
			Font.NORMAL);
	
	private static final String TITLE = "  Digital fundus Imaging Report";
	
	public static File generatePDF(PatientEntity patientObject,
			String direcotryPath, Context context, boolean isCropRequested) {
		Document document = new Document();
		
		File file = null;
		if (patientObject != null) {
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Remidio/"
					+ patientObject.getPatientId() + "_"
					+ patientObject.getName() + "_" + patientObject.getDOB()
					+ File.separator + "Report.pdf");
		}
		else if (direcotryPath != null && !direcotryPath.isEmpty()) {
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Remidio/" + direcotryPath
					+ File.separator + "Report.pdf");
		}
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			
			document.open();
			
			// Add Meta Data
			addMetaData(document);
			
			addContent(context, document, patientObject, isCropRequested);
			
			document.close();
			
			deleteCroppedImages(new File(
					Environment.getExternalStorageDirectory() + File.separator
							+ "Remidio/" + patientObject.getPatientId() + "_"
							+ patientObject.getName() + "_"
							+ patientObject.getDOB()));
			
		}
		catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException", e);
		}
		catch (DocumentException e) {
			Log.e(TAG, "DocumentException", e);
		}
		catch (MalformedURLException e) {
			Log.e(TAG, "MalformedURLException", e);
		}
		catch (IOException e) {
			Log.e(TAG, "IOException", e);
		}
		return file;
	}
	
	private static void addMetaData(Document document) {
		document.addTitle("Patient Report");
		document.addSubject("Report");
		document.addKeywords("Remedio,Eye,Report");
		document.addAuthor("Remedio App");
		document.addCreator("Remedio App");
	}
	
	private static void addContent(Context context, Document document,
			PatientEntity patientObject, boolean isCropRequested)
			throws DocumentException, MalformedURLException, IOException {
		Paragraph preface = new Paragraph();
		// We add one empty line
		addEmptyLine(preface, 1);
		
		// Lets write a big header
		Paragraph titleParagraph = new Paragraph();
		
		// titleParagraph.setAlignment(Element.ALIGN_CENTER);
		
		addHeader(context, titleParagraph, patientObject);
		
		preface.add(titleParagraph);
		
		addEmptyLine(preface, 1);
		
		// preface.add(new Paragraph(
		// "Check No "
		// + 123
		// +
		// "                                                        Check Date "
		// + new SimpleDateFormat("dd/MM/yyyy").format(System
		// .currentTimeMillis())));
		
		PdfPTable table = new PdfPTable(1);
		
		table.setTotalWidth(1250f);
		
		Paragraph cellParagraph = new Paragraph();
		
		PdfPTable innerDetailTable = new PdfPTable(2);
		
		innerDetailTable.setWidths(new int[] { 1, 3 });
		
		innerDetailTable.addCell(getCellWithoutBorder("   ID"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getPatientId()));
		innerDetailTable.addCell(getCellWithoutBorder("   Name"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getName()));
		innerDetailTable.addCell(getCellWithoutBorder("   Gender"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getGender()));
		innerDetailTable.addCell(getCellWithoutBorder("   Age"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getAge()));
		innerDetailTable.addCell(getCellWithoutBorder("   DOB"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getDOB()));
		innerDetailTable.addCell(getCellWithoutBorder("   Address"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getAddress()));
		innerDetailTable.addCell(getCellWithoutBorder("   Doctor"));
		innerDetailTable.addCell(getCellWithoutBorder(":   "
				+ patientObject.getDoctorName()));
		if (patientObject.getComment() != null
				&& !patientObject.getComment().isEmpty()) {
			innerDetailTable.addCell(getCellWithoutBorder("   Comment"));
			innerDetailTable.addCell(getCellWithoutBorder(":   "
					+ patientObject.getComment()));
		}
		innerDetailTable.addCell(getCellWithoutBorder(" "));
		innerDetailTable.addCell(getCellWithoutBorder(" "));
		
		cellParagraph.add(innerDetailTable);
		
		// cellParagraph.add(new Paragraph("\n   ID                 :" + "   "
		// + patientObject.getId() + "\n   Name               :" + "   "
		// + patientObject.getName() + "\n   Gender             :" + "   "
		// + patientObject.getGender() + "\n   Age                :"
		// + "   " + patientObject.getAge() + "\n   DOB                :"
		// + "   " + patientObject.getDOB() + "\n   Hospital Name      :"
		// + "   " + patientObject.getHospitalName()
		// + "\n   Doctor             :" + "   "
		// + patientObject.getDoctorName() + "\n", smallBold));
		
		PdfPCell cell = new PdfPCell(innerDetailTable);
		
		table.addCell(cell);
		
		preface.add(table);
		
		addEmptyLine(preface, 2);
		
		// addImages(document, patientObject.getPhotoFilePathList());
		
		document.add(preface);
		
		// if (isCropRequested) {
		// addImagesIntoTable(document, context);
		// }
		// else {
		addImagesIntoTable(document, patientObject.getPhotoFilePathList());
		// }
		
	}
	
	private static void deleteCroppedImages(File patientDirectory) {
		
		// Cropped Directory
		File croppedDirectory = new File(
				Environment.getExternalStorageDirectory() + File.separator
						+ "croppedPic");
		if (croppedDirectory.exists()) {
			for (String files : croppedDirectory.list()) {
				File f = new File(croppedDirectory.getAbsolutePath()
						+ File.separator + files);
				if (f.exists())
					f.delete();
			}
			croppedDirectory.delete();
		}
		
		// Patient Directory
		if (patientDirectory != null && patientDirectory.exists()) {
			String path = patientDirectory.getAbsolutePath();
			String[] files = patientDirectory.list();
			for (String file : files) {
				if (file.contains("_CROPPED")) {
					new File(path + File.separator + file).delete();
				}
			}
		}
	}
	
	private static void addHeader(Context context, Paragraph preface,
			PatientEntity patientObject) throws BadElementException,
			MalformedURLException, IOException {
		// Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.launch_icon);
		// if (bmp != null) {
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// bmp.compress(Bitmap.CompressFormat.PNG, 45, stream);
		// Image image = Image.getInstance(stream.toByteArray());
		// image.scaleAbsoluteHeight(25f);
		// image.scaleAbsoluteWidth(25f);
		// preface.add(new Phrase("                  "));
		// preface.add(new Chunk(image, 0, 0));
		// preface.add(new Phrase(patientObject.getHospitalName(), catFont));
		// }
		if (patientObject != null && patientObject.getHospitalName() != null
				&& !patientObject.getHospitalName().isEmpty()) {
			preface.add(new Phrase("             "
					+ patientObject.getHospitalName().toUpperCase(), catFont));
		}
		
	}
	
	private static void addImages(Document document, List<String> filePathList)
			throws MalformedURLException, IOException, DocumentException {
		float yPos = 505f;
		for (int i = 0; i < filePathList.size(); i++) {
			
			Matrix mat = new Matrix();
			mat.postRotate(90);
			
			Bitmap loadedImage = BitmapFactory.decodeFile(filePathList.get(i));
			
			Bitmap bmp = Bitmap.createBitmap(loadedImage, 0, 0,
					loadedImage.getWidth(), loadedImage.getHeight(), mat, true);
			if (bmp != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 45, stream);
				Image image = Image.getInstance(stream.toByteArray());
				if (bmp.getWidth() > bmp.getHeight()) {
					image.scaleAbsoluteHeight(130f);
					image.scaleAbsoluteWidth(210f);
				}
				else {
					image.scaleAbsoluteHeight(210f);
					image.scaleAbsoluteWidth(130f);
				}
				
				if (i % 2 == 0) {
					if (i > 0) {
						yPos -= 145f;
					}
					image.setAbsolutePosition(80f, yPos);
				}
				else {
					image.setAbsolutePosition(310f, yPos);
				}
				document.add(image);
			}
		}
	}
	
	private static void addImagesIntoTable(Document document,
			List<String> filePathList) throws MalformedURLException,
			IOException, DocumentException {
		float yPos = 505f;
		PdfPTable table = new PdfPTable(2);
		
		for (int i = 0; i < filePathList.size(); i++) {
			
			// Bitmap bmp = ImageHelper.applyOrientation(
			// BitmapFactory.decodeFile(filePathList.get(i)),
			// ImageHelper.resolveBitmapOrientation(filePathList.get(i)));
			
			// Matrix mat = new Matrix();
			// mat.postRotate(90);
			
			Bitmap bmp = BitmapFactory.decodeFile(filePathList.get(i));
			
			// Bitmap bmp = Bitmap.createBitmap(loadedImage, 0, 0,
			// loadedImage.getWidth(), loadedImage.getHeight(), mat, true);
			
			if (bmp != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 45, stream);
				Image image = Image.getInstance(stream.toByteArray());
				
				image.scaleAbsoluteHeight(bmp.getHeight() / 16);
				image.scaleAbsoluteWidth(bmp.getWidth() / 19);
				
				PdfPCell cell = new PdfPCell(image, true);
				
				Log.i(TAG,
						"bm hieght->" + bmp.getHeight() + ",cell height->"
								+ cell.getHeight() + ",imagehieght->"
								+ image.getHeight());
				
				Log.i(TAG, "bm width->" + bmp.getWidth() + ",cell width->"
						+ cell.getWidth() + ",imagewidth->" + image.getWidth());
				cell.setPadding(5);
				
				cell.setBorder(Rectangle.NO_BORDER);
				
				table.addCell(cell);
			}
		}
		
		if (filePathList.size() % 2 != 0) {
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		}
		
		document.add(table);
		
	}
	
	private static void addImagesIntoTable(Document document, Context context)
			throws MalformedURLException, IOException, DocumentException {
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "croppedPic");
		if (file.exists()) {
			String[] filePathList = file.list();
			float yPos = 505f;
			PdfPTable table = new PdfPTable(2);
			
			for (int i = 0; i < filePathList.length; i++) {
				
				// Bitmap bmp = ImageHelper.applyOrientation(
				// BitmapFactory.decodeFile(filePathList.get(i)),
				// ImageHelper.resolveBitmapOrientation(filePathList.get(i)));
				
				String imagePath = file.getAbsolutePath() + File.separator
						+ filePathList[i];
				Bitmap bmp = BitmapFactory.decodeFile(imagePath);
				
				if (bmp != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 45, stream);
					Image image = Image.getInstance(stream.toByteArray());
					
					image.scaleAbsoluteHeight(bmp.getHeight() / 16);
					image.scaleAbsoluteWidth(bmp.getWidth() / 19);
					
					PdfPCell cell = new PdfPCell(image, true);
					
					Log.i(TAG, "bm hieght->" + bmp.getHeight()
							+ ",cell height->" + cell.getHeight()
							+ ",imagehieght->" + image.getHeight());
					
					Log.i(TAG,
							"bm width->" + bmp.getWidth() + ",cell width->"
									+ cell.getWidth() + ",imagewidth->"
									+ image.getWidth());
					cell.setPadding(5);
					
					cell.setBorder(Rectangle.NO_BORDER);
					
					table.addCell(cell);
					
					File imageFile = new File(imagePath);
					if (imageFile.exists()) {
						imageFile.delete();
					}
				}
			}
			
			if (filePathList.length % 2 != 0) {
				PdfPCell cell = new PdfPCell();
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
			}
			document.add(table);
			
			file.delete();
		}
	}
	
	private static void addTable(Paragraph para, List<String> filePathList)
			throws BadElementException, MalformedURLException, IOException {
		PdfPTable table = new PdfPTable(2);
		
		// details cell
		PdfPCell cell = new PdfPCell(new Phrase("Name:viral"));
		cell.setColspan(2);
		
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Name:viral"));
		cell.setBorder(Rectangle.BOX);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		para.add(table);
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	private static PdfPCell getCellWithoutBorder(String data) {
		PdfPCell cell = new PdfPCell(new Phrase(data, smallBold));
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}
}
