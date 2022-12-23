package com.sample.framework.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.crypto.MasterKeyManager;
import com.airwatch.sdk.context.SDKContext;
import com.airwatch.sdk.context.SDKContextManager;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */
/*
This class Read TXT,PDF,MPG Files from SDCARD root folder and Encrypts and Decrypts the data
Background threads are launched to execute parallel encryption and decryption
 */
public class FWFileKeyManager extends AppBaseActivity {
    private ListView mListView;
    private TextView mTextViewEncrypt, mTextViewDecrypt,mTextViewFileName,mTextViewFileHeader;
    private ProgressBar spinner;
    private String tvenDecryptFileName;
    private String [] enDecyrptFileName; //names of Files selected by User for encryptDecrypt
    private static final int MODE_ENCRYPT=0;
    private static final int MODE_DECRYPT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fw_file_key_manager);
        mTextViewEncrypt = (TextView) findViewById(R.id.tv_file_encrypt);
        mTextViewDecrypt = (TextView) findViewById(R.id.tv_file_decrypt);
        mTextViewFileName= (TextView) findViewById(R.id.tv_file_info);
        mTextViewFileHeader=(TextView) findViewById(R.id.tv_fw_file_title);
        spinner=(ProgressBar)findViewById(R.id.pb_file_enrypt_decrypt);
        spinner.setVisibility(View.GONE);
        mTextViewFileHeader.setText("Files List :- ");
        ArrayList<String> fileList=readFileSDcard();//files that can be selected by user
        initializeListView(fileList);
    }

    /**
     * Initalize ListVIew with File names and implement a Dialog Prompt on Item Click
     * @param fileList
     */
    private void initializeListView(final ArrayList<String> fileList) {
        mListView=(ListView) findViewById(R.id.lv_file_name);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,R.layout.main_list_item,
                R.id.main_list_item_text,fileList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FWFileKeyManager.this);
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                        fileList.get(position));
                builder.setMessage("Add File " + fileList.get(position) +
                        "    " + file.length() / (1024 * 1024) + " MB")
                        .setTitle("Encrypt/Decrypt")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addUserFile(fileList.get(position));
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog promptDialog = builder.create();
                promptDialog.show();
            }
        });
    }

    /**
     * read pdf,txt,mpg from sdcard
     * @return ArrayList with File Name
     */
    private ArrayList<String> readFileSDcard() {
        File file =new File(Environment.getExternalStorageDirectory().getPath());
        File [] allFiles=file.listFiles();
        ArrayList<String> fileList=new ArrayList<>();
        for (File allFile : allFiles) {
            String filename = allFile.getName();
            if (filename.contains(".txt") || filename.contains(".pdf") || filename.contains(".mpg")) {
                fileList.add(filename);
            }
        }
        if(fileList.size()<=0){
            mTextViewFileName.setText("Not able to read .txt,.pdf,.mpg files from root SDcard");
        }
        return fileList;
    }

    /**
     * ADD user file to textView
     * @param fileName Name of File
     */
    private void addUserFile(String fileName){
        File file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
        if(file.exists()){
            String token=mTextViewFileName.getText().toString();
            token+=fileName+" ";
            mTextViewFileName.setText(token);
            Log.i("FILE ADD", "added " + token);

        }
        else{
            Toast.makeText(getApplicationContext(), "FILE WAS NOT FOUND", Toast.LENGTH_SHORT).show();
            Log.i("FILE ADD", "File not found");
        }
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_file_encrypt : mTextViewEncrypt.setText("");
                mTextViewDecrypt.setText("");
                encrypt();
                break;
            case R.id.btn_file_decrypt:  mTextViewDecrypt.setText("");
                mTextViewEncrypt.setText("");
                decrypt();
                break;
            case R.id.btn_clear_file_list: mTextViewFileName.setText("");
                break;
        }
    }


    /**
     * Launches a background Thread to Encrypt if FileSelected are valid
     */
    private void encrypt(){
        enDecyrptFileName =mTextViewFileName.getText().toString().split(" ");
        if(!checkFileName()) {
            Toast.makeText(getApplicationContext(), "Please select unique files "
                    , Toast.LENGTH_SHORT).show();
            mTextViewFileName.setText("");
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        tvenDecryptFileName="Encrypted files stored in SDcard/Encrypted/ as ";
        new backgroundTask(MODE_ENCRYPT).execute();
    }


    /**
     * Launches a background thread to Decrypt if FileSelected are valid
     */
    private void decrypt(){
        enDecyrptFileName =mTextViewFileName.getText().toString().split(" ");
        if(!checkFileName()) {
            Toast.makeText(getApplicationContext(), "Please select unique files "
                    , Toast.LENGTH_SHORT).show();
            mTextViewFileName.setText("");
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        tvenDecryptFileName="Decrypted files stored in SDcard/Decrypted/ as ";
        new backgroundTask(MODE_DECRYPT).execute();
    }

    /**
     * Function to check if the FilesSelected are unique or not
     * @return false if some files selected for encrypt/decrypt are same otherwise true
     */
    private boolean checkFileName() {
        if(enDecyrptFileName[0].equals(""))
            return false;
        for(int i=0;i< enDecyrptFileName.length;i++){
            for(int j=i+1;j< enDecyrptFileName.length;j++){
                if(enDecyrptFileName[i].equals(enDecyrptFileName[j]))
                    return false;
            }
        }
        return true;
    }
    private class backgroundTask extends AsyncTask<String, Void, String> {

        private int taskMode;
        protected backgroundTask(int mode){
            taskMode=mode;
        }
        @Override
        protected String doInBackground(String... params) {
            multiFileEncryptDecryptHelper(taskMode);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (taskMode == MODE_DECRYPT) {
                mTextViewDecrypt.setText(tvenDecryptFileName);
            }else{
                mTextViewEncrypt.setText(tvenDecryptFileName);
            }
            spinner.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    /**
     * Helper function to launch N Threads to Encrypt or Decrypt
     * where N is the number of FilesSelected
     * @param taskMode MODE_ENCRYPT or MODE_DECRYPT
     */
    private void multiFileEncryptDecryptHelper(int taskMode) {
        int threadSize = enDecyrptFileName.length;
        ExecutorService executor = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++) {
            Runnable worker = new MyRunnable(taskMode, i);
            executor.execute(worker);
        }
        executor.shutdown();
        //Wait Until all threads finish
        while (!executor.isTerminated()) {
        }
        Log.d("THREAD EXECUTOR", "Finished RUNNING ALL THREADS");

    }


    /**
     * Runnable class that launches encypt or decrypt thread
     */
    protected class MyRunnable implements Runnable {

        private int mode = -1;
        private int threadId=-1;

        MyRunnable(int mode,int threadID) {
            this.mode = mode;
            this.threadId=threadID;
        }

        @Override
        public void run() {
            if (mode == MODE_DECRYPT) {
                decryptThread(threadId);
            } else if (mode == MODE_ENCRYPT) {
                encryptThread(threadId);
            }
        }
    }

    /**
     * Encrypt thread, calls MasterKeyManager and encrypts file
     * @param threadID The ID of the thread executing task
     */
    private void encryptThread(int threadID) {
        SDKContext sdkContextManager = SDKContextManager.getSDKContext();
        MasterKeyManager masterKeyManager = sdkContextManager.getKeyManager();
        checkFolderPath("/Encrypted/");
        try{
            File inFile = new File(Environment.getExternalStorageDirectory().getPath()
                    +"/"+ enDecyrptFileName[threadID]);
            File outFile = new File(Environment.getExternalStorageDirectory().getPath()
                    +"/Encrypted/Encrypted_"+ enDecyrptFileName[threadID]);
            if(inFile.exists()) {
                masterKeyManager.createKeyStore();
                masterKeyManager.encryptFile(inFile, outFile);
                tvenDecryptFileName+="Encrypted_"+enDecyrptFileName[threadID]+" ";
            }
            else
                tvenDecryptFileName="Unable to read from input file";
        }catch(Exception e){
            Log.e("ENCRYPT THREAD",e.getMessage());
        }

        Log.i("ENCRYPT THREAD","Encrypted file "+ enDecyrptFileName[threadID]+" by Thread "+threadID);
    }

    /**
     * Decrypt thread , calls MasterKeyManager and decrypts the encrypted file.
     * If the file that is to be decrypted is corrupt or not found function quits
     * @param threadID The ID of the thread executing task
     */
    private void decryptThread(int threadID) {
        SDKContext sdkContextManager = SDKContextManager.getSDKContext();
        MasterKeyManager masterKeyManager = sdkContextManager.getKeyManager();
        checkFolderPath("/Encrypted/");
        checkFolderPath("/Decrypted/");
        File inFile,outFile;
        try {
            inFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/Encrypted/Encrypted_" + enDecyrptFileName[threadID]);
            outFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/Decrypted/Decrypted_" + enDecyrptFileName[threadID]);
            if(inFile.exists()){
                masterKeyManager.decryptFile(inFile, outFile);
                tvenDecryptFileName+="Decrypted_"+enDecyrptFileName[threadID] + " ";
            }
            else
                tvenDecryptFileName="ERROR file was not Encrypted";
        }catch (Exception e){
            Log.e("DECRYPT THREAD",e.getMessage());
        }
        Log.i("DECRYPT THREAD","Decrypted file "+ enDecyrptFileName[threadID]+" by Thread "+threadID);
    }

    /**
     * Checks if the path exists or not and creates one if it does not exist
     * @param path The file path eg. /Encrypted/
     */
    private void checkFolderPath(String path) {
        File fpath = new File(Environment.getExternalStorageDirectory().getPath()+path);
        if(!fpath.exists()){
            fpath.mkdirs();
            fpath.setExecutable(true);
            fpath.setReadable(true);
            fpath.setWritable(true);
        }
    }
}
