package kr.ac.kopo.reportservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int LOCATION_REQUEST_CODE = 300;
    private static final int REQUEST_CODE_PERMISSIONS = 500;
    private ImageView imageView;
    private Uri imageUri;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        checkAndRequestPermissions();

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnGallery = findViewById(R.id.btnGallery);
        Button btnSend = findViewById(R.id.btnSend);

        btnCamera.setOnClickListener(v -> openCamera());
        btnSend.setOnClickListener(v -> sendData());

        btnGallery.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                getCurrentLocationAndSendData();
            } else {
                handlePermissionDenial();
            }
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS},
                    REQUEST_CODE_PERMISSIONS);
        } else {
            // 권한이 이미 허용된 경우 전화번호를 가져오는 메서드를 호출합니다.
            getPhoneNumber();
        }
    }

    private void handlePermissionDenial() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showSettingsDialog();
        } else {
            showPermissionExplanationDialog();
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage("이 기능을 사용하려면 권한이 필요합니다. 권한을 허용하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> checkAndRequestPermissions())
                .setNegativeButton("아니요", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 설정")
                .setMessage("권한을 허용하지 않았습니다. 앱 설정으로 이동하여 권한을 수동으로 설정하시겠습니까?")
                .setPositiveButton("설정", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            imageView.setImageURI(imageUri);
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "kr.ac.kopo.reportservice.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            checkAndRequestPermissions();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    private String getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return null;
        }
        return telephonyManager.getLine1Number();
    }

    private void sendData() {
        getCurrentLocationAndSendData();
    }

    private void getCurrentLocationAndSendData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        sendDataToServer();
                    } else {
                        showErrorDialog("위치 정보를 가져올 수 없습니다.");
                    }
                });
    }

    private void sendDataToServer() {
        // 이미지 데이터를 바이트 배열로 가져오기
        byte[] imageBytes;
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
            Log.d("imageBytes", Arrays.toString(imageBytes));
        } catch (Exception e) {
            showErrorDialog("이미지 처리 중 오류 발생: " + e.getMessage());
            return;
        }

        String phoneNumber = getPhoneNumber();
        Log.d("PhoneNumber", phoneNumber);

        // JSON 데이터 생성
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNumber", phoneNumber);
            Log.d("JSONData", jsonObject.toString());
        } catch (JSONException e) {
            showErrorDialog("JSON 객체 생성 실패");
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃을 30초로 설정
                .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃을 30초로 설정
                .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃을 30초로 설정
                .build();


        // RequestBody를 application/octet-stream으로 설정
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), imageBytes);

        // 요청 헤더에 위치 정보 추가
        Request request = new Request.Builder()
                .url("http://192.168.24.188:8080/send")
                .post(requestBody)
                .addHeader("latitude", String.valueOf(latitude))
                .addHeader("longitude", String.valueOf(longitude))
                .addHeader("phoneNumber", phoneNumber)
                .build();

        Log.d("request", String.valueOf(request));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showErrorDialog("서버 요청 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showErrorDialog("서버 응답 실패: " + response.code());
                } else {
                    showSuccessDialog("데이터 전송 성공");
                }
            }
        });
    }

    private void showErrorDialog(String message) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("오류")
                .setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
    }

    private void showSuccessDialog(String message) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("성공")
                .setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
    }
}
