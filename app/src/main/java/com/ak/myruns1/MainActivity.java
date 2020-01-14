package com.ak.myruns1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import com.soundcloud.android.crop.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editName;
    EditText editEmail;
    EditText editNumber;
    EditText editClass;
    EditText editMajor;

    Uri imgUri;
    File imgFile;
    String imgFileName = "xd.jpg";
    public static final int CAMERA_REQUEST_CODE = 0;
    String address;
    String name;
    String email;
    String phone;
    int gender=-1;
    int classNumber=0;
    String major;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("cache", MODE_PRIVATE);

        editName = findViewById(R.id.editname);
        editEmail = findViewById(R.id.editemail);
        editNumber = findViewById(R.id.editnumber);
        editClass = findViewById(R.id.editclass);
        editMajor = findViewById(R.id.editmajor);
        RadioButton femaleButton = findViewById(R.id.radioFemale);
        RadioButton maleButton = findViewById(R.id.radioMale);
        imageView = findViewById(R.id.imageProfile);
        Util.checkPermission(this);

        imgFile = new File(getExternalFilesDir(null), imgFileName);
        imgUri = FileProvider.getUriForFile(this,"com.xd.testcamera", imgFile); //is this authority okay?

        if(savedInstanceState != null) {
            address = savedInstanceState.getString("tag"); //remove later
            name = savedInstanceState.getString("name");
            email = savedInstanceState.getString("email");
            phone = savedInstanceState.getString("phone");
            classNumber = savedInstanceState.getInt("class");
            major = savedInstanceState.getString("major");
            gender = savedInstanceState.getInt("gender",-1);
        }

        else {
            name = prefs.getString("name", null);
            email = prefs.getString("email", null);
            phone = prefs.getString("phone", null);
            classNumber = prefs.getInt("class", 0);
            major = prefs.getString("major", null);
            gender = prefs.getInt("gender", -1);
            address = prefs.getString("tag",null);
        }

        if (name != null) editName.setText(name);
        if (email != null) editEmail.setText(email);
        if (phone != null) editNumber.setText(phone);
        if (classNumber != 0) editClass.setText(String.valueOf(classNumber));
        if (major != null) editMajor.setText(major);

        if(gender != -1) {
            if (gender==1)femaleButton.setChecked(true);
            else maleButton.setChecked(true);
        }

        if (address !=null)  imageView.setImageURI(Uri.fromFile(new File(address)));
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("tag", address);
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putInt("class", classNumber);
        bundle.putString("major", major);
        bundle.putInt("gender", gender);
    }

    public void onChangePhotoClicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            imageView.setImageURI(null);
            Crop.of(imgUri, Uri.fromFile(new File(getCacheDir(), "cropped"))).asSquare().start(this);
        }

        else{
            if (data != null) {
                imageView.setImageURI(Crop.getOutput(data));
                address = Uri.fromFile(new File(getCacheDir(), "cropped")).getPath();
            }
            else {
                imageView.setImageURI(imgUri);
                address = imgUri.getPath();
            }
        }
    }

    public void onRadioButtonClicked(View view) {

        switch(view.getId()) {
            case R.id.radioFemale:
                gender = 1;
                break;
            case R.id.radioMale:
                gender = 0;
                break;
        }
    }

    public void saveProfile(View view) {
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        phone = editNumber.getText().toString();
        if (!editClass.getText().toString().isEmpty()) classNumber = Integer.parseInt(editClass.getText().toString());
        else classNumber=0;
        major = editMajor.getText().toString();

        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("phone",phone);
        editor.putInt("class",classNumber);
        editor.putString("major",major);
        editor.putInt("gender",gender);
        editor.putString("tag",address);
        editor.apply();

        Toast.makeText(getBaseContext(),getText(R.string.save_msg),Toast.LENGTH_LONG).show();
        finish();
    }

    public void cancelInfo(View view) {
        finish();
    }

}
