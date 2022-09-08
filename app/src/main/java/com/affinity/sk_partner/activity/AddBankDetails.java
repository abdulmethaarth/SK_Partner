package com.affinity.sk_partner.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affinity.sk_partner.Api;
import com.affinity.sk_partner.BankDetails;
import com.affinity.sk_partner.CreateResponse;
import com.affinity.sk_partner.R;
import com.affinity.sk_partner.pojoClasses.Constants;
import com.affinity.sk_partner.pojoClasses.ImageFilePath;
import com.affinity.sk_partner.pojoClasses.RetrofitClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBankDetails extends AppCompatActivity {

    EditText bank_name,holder_name,acc_no,ifsc_code,branch_name;
    Button bank_dtls_nxt,check_branch ;
    boolean connected = false;
    String mobileno,driver_id;
    String pass_img_string;
    Bitmap pssbkbitmap;
    TextView layout_open_camera,layout_open_gallery;
    Dialog OpenCameraDialog;
    RelativeLayout layout_open_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_details);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driver_id = prefs.getString(Constants.driver_id, "");
        mobileno  = prefs.getString(Constants.mobileno, "");

        bank_name = (EditText)findViewById(R.id.bank_name);
        branch_name = (EditText)findViewById(R.id.branch_name);
        holder_name = (EditText)findViewById(R.id.holder_name);
        acc_no = (EditText)findViewById(R.id.acc_no);
        ifsc_code = (EditText)findViewById(R.id.ifsc_code);
        bank_dtls_nxt = (Button)findViewById(R.id.bank_dtls_nxt);
        check_branch = (Button)findViewById(R.id.check_branch);

        OpenCameraDialog = new Dialog(AddBankDetails.this, android.R.style.Theme_Translucent_NoTitleBar);
        OpenCameraDialog.setContentView(R.layout.camera_dialog_layout);
        layout_open_camera = (TextView) OpenCameraDialog.findViewById(R.id.layout_open_camera);
        layout_open_gallery = (TextView) OpenCameraDialog.findViewById(R.id.layout_open_gallery);
        layout_open_cancel = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_cancel);

        layout_open_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraDialog.cancel();
            }
        });
        check_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ifsc_code.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter IFSC code", Snackbar.LENGTH_LONG).show();
                    return;
                }
                else {
                    JsonObject gsonObject = new JsonObject();
                    try {

                        JSONObject jsonObj_ = new JSONObject();
                        jsonObj_.put("ifsc", ifsc_code.getText().toString());
                        JsonParser jsonParser = new JsonParser();
                        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());
                        Log.e("MY gson.JSON:  ", "AS PARAMETER  " + gsonObject);
                        // Toast.makeText(this, "result"+gsonObject, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Api myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
                    Call<JsonObject> call = myApi.getBankDetails(gsonObject);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            if (response.isSuccessful()) {
                                String jsonresponse = response.body().toString();
                                Snackbar.make(findViewById(android.R.id.content), "Sucess", Snackbar.LENGTH_LONG).show();
                                responseBinding(jsonresponse);

                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "eror"+"Please enter correct code", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Check your internet.", Snackbar.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

        bank_dtls_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank_name.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter Bank name", Snackbar.LENGTH_LONG).show();
                    return;
                }else if (holder_name.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter acc holder name", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (acc_no.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter acc no", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (ifsc_code.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter IFSC code", Snackbar.LENGTH_LONG).show();
                    return;
                }else{
                    bankDetailsUpload();
                }

            }
        });
    }

    private void responseBinding(String response) {
        try {
            //getting the whole json object from the response
            JSONObject obj = new JSONObject(response);
            if(obj.optString("status").equalsIgnoreCase("true")){

                ArrayList<BankDetails> retroModelArrayList = new ArrayList<>();
                JSONArray dataArray  = obj.getJSONArray("response");

                for (int i = 0; i < dataArray.length(); i++) {

                    BankDetails retroModel = new BankDetails();
                    JSONObject dataobj = dataArray.getJSONObject(i);

                   /* bank_name.setText(dataobj.getString("BRANCH"));
                    bank_name.setText(dataobj.getString("BANK"));*/
                    retroModel.setBANK(dataobj.getString("BANK"));
                    retroModel.setBRANCH(dataobj.getString("BRANCH"));

                    retroModelArrayList.add(retroModel);

                }

                for (int j = 0; j < retroModelArrayList.size(); j++){
                    bank_name.setText(retroModelArrayList.get(j).getBANK());
                    branch_name.setText(retroModelArrayList.get(j).getBRANCH());
                }

            }else {
                Toast.makeText(AddBankDetails.this, obj.optString("message")+"", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void bankDetailsUpload() {
        Intent getDatas = getIntent();
        String pancard = String.valueOf(getDatas.getStringExtra("pan_img"));
        String driverProfile = String.valueOf(getDatas.getStringExtra("driver_img"));
        String licenseFront = String.valueOf(getDatas.getStringExtra("license_front"));
        String licenseBack = String.valueOf(getDatas.getStringExtra("license_back"));
        String phoneNo = String.valueOf(getDatas.getStringExtra("mobileno"));
        String drivername = String.valueOf(getDatas.getStringExtra("fullname"));
        String mailId = String.valueOf( getDatas.getStringExtra("email_id"));
        String driver_dob = String.valueOf(getDatas.getStringExtra("dob"));
        String panNo = String.valueOf(getDatas.getStringExtra("panNo"));
        String licenceNo = String.valueOf(getDatas.getStringExtra("licenceNO"));

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            try {

                String uploadId = UUID.randomUUID().toString();
                //Creating a multi part request
                new MultipartUploadRequest(AddBankDetails.this, uploadId, Constants.REGISTER5)
                        .addFileToUpload(licenseBack, "licenceback") //Adding file
                        .addFileToUpload(licenseFront, "licencefront") //Adding file
                        .addFileToUpload(pancard,"pancardimg" ) //Adding file
                        .addFileToUpload(driverProfile,"driverimage" ) //Adding file
                        .addParameter("ac_bank", bank_name.getText().toString())
                        .addParameter("ac_holder_name", holder_name.getText().toString())
                        .addParameter("ac_number", acc_no.getText().toString())
                        .addParameter("mobile", phoneNo)
                        .addParameter("driver_name", drivername)
                        .addParameter("email",mailId)
                        .addParameter("dob", driver_dob)
                        .addParameter("pancard_number", panNo)
                        .addParameter("licence",licenceNo )
                        .addParameter("ac_ifsc", ifsc_code.getText().toString())
                        .setMaxRetries(3)
                        .startUpload();//Starting the upload
                Toast.makeText(AddBankDetails.this, "Succeed", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(Constants.KEY_LOGGED_IN,true);
                editor.apply();
                startActivity(new Intent(AddBankDetails.this, DriverHomeActivity.class));
            } catch (FileNotFoundException f) {
                //   Common.showMkError(SignUpActivity.this, getResources().getString(R.string.check_internet));
                Toast.makeText(AddBankDetails.this, "check internet", Toast.LENGTH_SHORT).show();
            } catch (Exception exc) {
            }
        }
        else{
            connected = false;
            Snackbar.make(findViewById(android.R.id.content), "Check your internet connection.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.really_exit))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //  HomeActivity.super.onBackPressed();
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }
}
