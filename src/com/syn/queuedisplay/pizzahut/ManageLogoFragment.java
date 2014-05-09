package com.syn.queuedisplay.pizzahut;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.j1tth4.util.FileManager;
import com.j1tth4.util.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ManageLogoFragment extends Fragment{
	
	public static final int RESULT_LOAD_IMAGE = 1;

	public static final String LOGO_DIR = "pRoMiSeQueueLogo";
	public static final String FILE_NAME = "logo.png";
	
	private Bitmap mBitmap;
	private ImageView mImgLogoPreview;
	
	public ManageLogoFragment(){	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.manage_logo_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_save:
			saveImage();
			return true;
		case R.id.action_delete:
			deleteImage();
			return true;
		case R.id.action_cancel:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.manage_logo_layout, container, false);
		mImgLogoPreview = (ImageView) rootView.findViewById(R.id.imgLogoPreview);
		Button btnSelectFile = (Button) rootView.findViewById(R.id.btnSelectFile);
		btnSelectFile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 
                startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
			
		});
		
		loadImage();
		return rootView;
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
         
            mBitmap = BitmapFactory.decodeFile(picturePath);
            mImgLogoPreview.setImageBitmap(mBitmap);
         
        }
    }
	
	private void loadImage(){
		FileManager fm = new FileManager(getActivity(), LOGO_DIR);
		mBitmap = BitmapFactory.decodeFile(fm.getFile(FILE_NAME).getPath());
		mImgLogoPreview.setImageBitmap(mBitmap);
	}
	
	private void deleteImage(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.manage_logo);
		builder.setMessage(R.string.confirm_delete_file);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FileManager fm = new FileManager(getActivity(), LOGO_DIR);
				fm.clear();
				mImgLogoPreview.setImageBitmap(null);
				new AlertDialog.Builder(getActivity())
				.setTitle(R.string.manage_logo)
				.setMessage(R.string.delete_success)
				.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void saveImage(){
		FileManager fm = new FileManager(getActivity(), LOGO_DIR);
		try {
			OutputStream stream = new FileOutputStream(fm.getFile(FILE_NAME));
			mBitmap.compress(CompressFormat.PNG, 100, stream);
			
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.manage_logo)
			.setMessage(R.string.save_logo_success)
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
		} catch (FileNotFoundException e) {
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.manage_logo)
			.setMessage("Error when save image : " + e.getMessage())
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			Logger.appendLog(getActivity(), QueueApplication.LOG_DIR, 
					QueueApplication.LOG_FILE_NAME, "Error when save image : " + e.getMessage());
		}
	}
}
