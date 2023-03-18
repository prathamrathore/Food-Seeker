package com.example.pc.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private static final int PERMISSION_REQUEST_CODE = 33;
    private static final int PERMISSION_REQUEST_CODE_WRITE = 333;
    ImageView imageView;
    //Uri filepath;
    Bitmap bmp;
    Spinner spinner;
    File file;
    ProgressDialog dialog;
    final int PIC_CROP = 1;
    private Uri picUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinner);
        imageView.setOnClickListener(this);
        Log.d("info", "inside onCreate class");

        spinner.setOnItemSelectedListener(this);
        dialog = new ProgressDialog(this);

        if (!checkPermission()) {
            requestPermission();
        }
//        check();
    }

    private void check() {
        FirebaseVisionImage image;
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/myimg.jpg");
        try {
            image = FirebaseVisionImage.fromFilePath(this,Uri.fromFile(f));
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            textRecognizer.processImage(image).addOnSuccessListener(this, new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String text = firebaseVisionText.getText();
                    Toast.makeText(MainActivity.this, "TEXT IS: "+text, Toast.LENGTH_SHORT).show();
                }
            }).
                    addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   /* Extract text Commented
   private void check2(){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(image).addOnSuccessListener(this, new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //builder.setMessage(firebaseVisionText.getText());
                List<FirebaseVisionText.TextBlock> textBlockList = firebaseVisionText.getTextBlocks();
                final StringBuffer sb = new StringBuffer();
                for(FirebaseVisionText.TextBlock textBlock:textBlockList){
                    String strblock = textBlock.getText();
                    sb.append(strblock);
                    sb.append("\n"+textBlock.getConfidence());
                    sb.append("\n=========\n");
                }
                builder.setMessage(sb.toString());
                builder.setTitle("TEXT DETECTED");
                builder.setPositiveButton(
                        "Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Context context;
                                //saving the file

                                //storeImage(bmp);
                                writeToFile(sb.toString(),MainActivity.this);
                                dialog.dismiss();

                            }
                        });
                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.create();
                builder.show();
            }
        }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }Extract text Commented*/

    /*private void detectLandmark(){
        dialog.setMessage("Detecting Landmark..please wait");
        dialog.show();
        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder().
                        setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL).setMaxResults(15).
                        build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionCloudLandmarkDetector detector = FirebaseVision.getInstance().getVisionCloudLandmarkDetector();
        detector.detectInImage(image).
                addOnSuccessListener(this, new OnSuccessListener<List<FirebaseVisionCloudLandmark>>() {
                    StringBuffer sb = new StringBuffer();
                    @Override
                    public void onSuccess(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
                        for(FirebaseVisionCloudLandmark landmark:firebaseVisionCloudLandmarks){
                            sb.append("Accuracy: "+landmark.getConfidence()+"\n");
                            sb.append("Landmark: "+landmark.getLandmark()+"\n");
                            for(FirebaseVisionLatLng latLng:landmark.getLocations()){
                                sb.append("Latitude: "+latLng.getLatitude()+"\n");
                                sb.append("Longitude: "+latLng.getLongitude()+"\n");
                            }
                            sb.append("==========\n");
                        }
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(sb.toString());
                        builder.show();
                    }
                }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
    private void imageLabelDetect(){
        dialog.setMessage("Detecting Labels..please wait");
        dialog.show();
        FirebaseVisionLabelDetectorOptions options = new
                FirebaseVisionLabelDetectorOptions.Builder().setConfidenceThreshold(0.8f).build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionLabelDetector labelDetector = FirebaseVision.getInstance().getVisionLabelDetector();
        labelDetector.detectInImage(image).
                addOnSuccessListener(this, new OnSuccessListener<List<FirebaseVisionLabel>>() {
                    StringBuffer sb = new StringBuffer();
                    int i=0;
                    @Override
                    public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                        for(FirebaseVisionLabel label:firebaseVisionLabels){
                            sb.append("Accuracy: "+label.getConfidence()+"\n");
                            sb.append("Object : "+label.getLabel()+"\n");
                            //sb.append("Id: "+label.getEntityId());
                            sb.append("===========\n");
                            i++;
                        }
                        dialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage(sb.toString());
                        alert.setTitle("Image Labeling: Total "+i+" objects found");
                        alert.setPositiveButton(
                                "Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Context context;
        //saving the file

                                       //storeImage(bmp);
                                        writeToFile(sb.toString(),MainActivity.this);
                                        dialog.dismiss();

                                    }
                                });
                        alert.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        alert.create();
                        alert.show();

                    }
                }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void writeToFile(String data,Context context) {
        try {
            File mydir =new File(Environment.getExternalStorageDirectory() +"/ODAT/"); //Creating an internal dir;
            if (!mydir.exists())
            {
                mydir.mkdir();

            }

            Date d = new Date();
            CharSequence s  = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
          //  File pictureFile =new File(Environment.getExternalStorageDirectory() +"/ODAT/"+"1"); //Creating an internal dir;

            File pictureFile = new File(mydir, s.toString() + ".txt");




            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(pictureFile));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void storeImage(Bitmap image) {
        File mydir =new File(Environment.getExternalStorageDirectory() +"/ODAT/"); //Creating an internal dir;
        if (!mydir.exists())
        {
            mydir.mkdir();

        }
        File pictureFile =new File(Environment.getExternalStorageDirectory() +"/ODAT/"+"1"); //Creating an internal dir;

        String TAG="sdfsd";
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
     /*Barcode commented
    private void detectBarcode(){
        dialog.setMessage("Scanning Barcode..please wait");
        dialog.show();
        FirebaseVisionBarcodeDetectorOptions options
                = new FirebaseVisionBarcodeDetectorOptions.Builder().
                setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE).build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionBarcodeDetector barcodeDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        barcodeDetector.detectInImage(image).
                addOnSuccessListener(this, new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    StringBuffer sb = new StringBuffer();
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        for(FirebaseVisionBarcode barcode:firebaseVisionBarcodes){
                            sb.append("Raw value:  "+barcode.getRawValue()+"\n");
                            int value= barcode.getValueType();
                            switch (value){
                                case FirebaseVisionBarcode.TYPE_PHONE:
                                    sb.append("Phone Number is: "+barcode.getPhone().getNumber());
                                    break;
                            }
                            sb.append("==================\n");
                        }
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(sb.toString());

                        builder.setPositiveButton(
                                "Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Context context;
                                        //saving the file

                                        //storeImage(bmp);
                                        writeToFile(sb.toString(),MainActivity.this);
                                        dialog.dismiss();

                                    }
                                });
                        builder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    bar code commented*/


    private void performCrop(Uri picUri) {
        try {
            Log.d("info", "inside spinner class");

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anne) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



   private void faceDetect(){
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder().
                setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE).
                setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS).
                setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS).
                setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS).
                build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        faceDetector.detectInImage(image).addOnSuccessListener(this, new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> faces) {
               // Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                for(FirebaseVisionFace face:faces){
                    Rect bounds = face.getBoundingBox();
                    FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                    if (leftEar != null) {
                        FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                    }
                   /*List<FirebaseVisionPoint> leftEyeContour =
                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                    List<FirebaseVisionPoint> upperLipBottomContour =
                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();*/
                    if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        float smileProb = face.getSmilingProbability();
                        if(smileProb >= 0.7) {
                            Toast.makeText(MainActivity.this, "SMILING FACE DETECTED", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(MainActivity.this, "SERIOUS FACE DETECTED", Toast.LENGTH_SHORT).show();
                        }
                    }
                    /*if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                        Toast.makeText(MainActivity.this, "RIGHT EYE: "+rightEyeOpenProb, Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == imageView){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(MainActivity.this);

          /*  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Select the image for Back View").setCancelable(true);
            builder.setPositiveButton(
                    "Camera", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent n = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           // file = new File(Environment.getExternalStorageDirectory(),"file"+String.valueOf(System.currentTimeMillis())+".jpg");
                            //picUri = Uri.fromFile(file);
                           // n.putExtra(MediaStore.EXTRA_OUTPUT,picUri);
                          //  n.putExtra("return-data",true);
                        //    startActivityForResult(n, 2347);
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(MainActivity.this);

                        }
                    });

            builder.setNegativeButton(
                    "Gallery",
                    new DialogInterface.OnClickListener() {
                        @SuppressLint("IntentReset")
                        public void onClick(DialogInterface dialog, int id) {
                            @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, 2348);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            */
        }

   }

Uri outputUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                picUri =resultUri;

                bmp=BitmapFactory.decodeFile(picUri.getPath());
                imageView.setImageBitmap(bmp);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }

     /*   else
        if(requestCode == 2348 && resultCode == RESULT_OK && data !=null){

            try {
             bmp  = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
             imageView.setImageBitmap(bmp);
                picUri=data.getData();
                Log.e("sdfsd","ff "+picUri);
               // Crop.of(picUri, outputUri).asSquare().start(this);
                //   performCrop(picUri);            //crop is not working
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
      */
        else if ((requestCode == 2347 && resultCode == RESULT_OK && data != null)) {

          /*  Uri file = null;
            if (data != null){
                file = data.getData();
                String uri = null;
                if (file != null) {
                    uri = file.getPath();
                }
     */
                try {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        bmp = (Bitmap) extras.get("data");
                      //  performCrop(picUri);
                     //   Bitmap bmp = (Bitmap) data.getExtras().get("data");
                       // imageView.setImageBitmap(bmp);
                    }
                    //imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }


       else  if (requestCode == PIC_CROP && resultCode == RESULT_OK ) {
            if (data != null) {
                picUri = data.getData();
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                imageView.setImageBitmap(selectedBitmap);
            }
        }
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("info", "inside spinner class");
        String item =(String)adapterView.getSelectedItem();
        switch (item){
            /*case "EXTRACT TEXT":
                check2();
                break;*/
            //case "DETECT FACE":
              // faceDetect();
                //break;
            case "LABEL IMAGES":
                imageLabelDetect();
                break;
            //case "DETECT LANDMARK":
              // detectLandmark();
                //break;
           /* case "BARCODE SCANNING":
                detectBarcode();
                break;*/
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermission();

                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}


