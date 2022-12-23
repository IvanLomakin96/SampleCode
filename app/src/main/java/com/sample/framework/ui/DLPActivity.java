package com.sample.framework.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.airwatch.dlp.openIn.UriOpenerFactory;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.net.URISyntaxException;

public class DLPActivity extends AppBaseActivity {
    EditText editTextUri;
    EditText editTextFile;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "DLP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlp);
        editTextUri = (EditText) findViewById(R.id.edit_uri);
        editTextFile = (EditText) findViewById(R.id.edit_file);

    }

    public void openUri(View view) {
        Uri uri = Uri.parse(editTextUri.getText().toString());
        UriOpenerFactory.getInstance().openUri(DLPActivity.this, uri);
    }

    public void openFile(View view) {
        String filePath = editTextFile.getText().toString().trim();
        if (filePath.startsWith("content://")) {
            Uri uri = Uri.parse(filePath);
            UriOpenerFactory.getInstance().openUri(DLPActivity.this, uri);
        } else {
            UriOpenerFactory.getInstance().openFile(DLPActivity.this, filePath);
        }

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        if (uri != null) {
                            editTextFile.setText(uri.toString());
                            Log.d(TAG, "File Uri: " + uri.getPath());
                        }
                    } catch (Exception e) {
                        Logger.e("URI Syntax Exceptioon", e);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Logger.e(TAG, "Exception", e);
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void chooseFile(View view) {
        showFileChooser();
    }
}