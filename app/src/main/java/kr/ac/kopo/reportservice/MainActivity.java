package kr.ac.kopo.reportservice;

import androidx.annotation.NonNull;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
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
        btnSend.setOnClickListener(v -> getCurrentLocationAndSendData(imageUri));

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
                getCurrentLocationAndSendData(imageUri);
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
            if (data != null && data.getData() != null) {
                imageUri = data.getData();  // URI를 저장
                imageView.setImageURI(imageUri);  // ImageView에 설정
                extractLocationFromImage(imageUri);  // URI를 전달하여 이미지에서 위치 정보 추출
            } else {
                Log.e("MainActivity", "Received null data or uri");
            }
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

    private double[] extractLocationFromImage(Uri imageUri) {
        double[] latLong = new double[]{0, 0};  // 기본값 설정

        if (imageUri == null) {
            Log.e("extractLocationFromImage", "Image Uri is null");
            return latLong;  // 위치 정보를 추출할 수 없을 경우 기본값 반환
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);

            float[] latLongFloat = new float[2];
            if (exif.getLatLong(latLongFloat)) {
                latLong[0] = latLongFloat[0];  // 위도
                latLong[1] = latLongFloat[1];  // 경도
            } else {
                Log.d("extractLocationFromImage", "No EXIF location data found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("extractLocationFromImage", "Failed to extract location from image: " + e.getMessage());
        }

        return latLong;
    }

    private String extractDateTimeFromImage(Uri imageUri) {
        String dateTime = null;
        try {
            // EXIF 데이터 읽기
            ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(imageUri));

            // 촬영일시 추출
            dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            if (dateTime == null) {
                dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME); // 대체로 사용될 수 있는 다른 태그
            }

            if (dateTime != null) {
                Log.d("DateTime", "촬영일시: " + dateTime);
            } else {
                showErrorDialog("촬영일시 정보가 EXIF 데이터에 없습니다.");
            }
        } catch (IOException e) {
            showErrorDialog("EXIF 데이터 읽기 실패: " + e.getMessage());
        }
        return dateTime;
    }

    private void getCurrentLocationAndSendData(Uri imageUri) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // 이미지에서 위치 및 날짜 정보 추출
        double[] extractedLocation = extractLocationFromImage(imageUri);
        final String[] extractedDateTime = {extractDateTimeFromImage(imageUri)};
        Log.d("image location", Arrays.toString(extractedLocation));
        Log.d("image Datetime", extractedDateTime[0]);

        // 현재 위치 가져오기
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    double latitude;
                    double longitude;

                    if (extractedLocation[0] != 0 && extractedLocation[1] != 0) {
                        // 추출된 위치 사용
                        latitude = extractedLocation[0];
                        longitude = extractedLocation[1];
                    } else {
                        // 유효한 EXIF 위치 정보가 없을 경우 현재 위치 사용
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            showErrorDialog("위치 정보를 가져올 수 없습니다.");
                            return;
                        }
                    }

                    // 날짜 정보가 없을 경우 현재 날짜 사용
                    if (extractedDateTime[0] == null) {
                        extractedDateTime[0] = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    }

                    // 데이터 서버로 전송
                    sendDataToServer(latitude, longitude, extractedDateTime[0], imageUri);
                });
    }
    //로딩바 만드는 함수
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);  // 다른 곳을 눌러도 닫히지 않도록 설정

        // LinearLayout 생성하여 ProgressBar와 TextView를 함께 추가
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);  // 수직 배치
        layout.setPadding(50, 50, 50, 50);  // 여백 추가
        layout.setGravity(Gravity.CENTER);  // 가운데 정렬

        // ProgressBar 추가
        ProgressBar progressBar = new ProgressBar(this);
        layout.addView(progressBar);  // LinearLayout에 ProgressBar 추가

        // TextView로 "로딩중..." 메시지 추가
        TextView loadingMessage = new TextView(this);
        loadingMessage.setText("로딩중...");
        loadingMessage.setGravity(Gravity.CENTER);  // 텍스트를 가운데 정렬
        layout.addView(loadingMessage);  // LinearLayout에 TextView 추가

        builder.setView(layout);

        progressDialog = builder.create();
        progressDialog.show();
    }
    //로딩바 숨기는 함수
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void sendDataToServer(double latitude, double longitude, String dateTime, Uri imageUri) {
        showLoadingDialog();
        // 이미지 데이터를 바이트 배열로 가져오기
        byte[] imageBytes;
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            showErrorDialog("이미지 처리 중 오류 발생: " + e.getMessage());
            return;
        }

        String phoneNumber = getPhoneNumber();
        Log.d("PhoneNumber", phoneNumber);

        // 전송할 데이터 조합
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("phoneNumber:").append(phoneNumber).append("\n");
        stringBuilder.append("latitude:").append(latitude).append("\n");
        stringBuilder.append("longitude:").append(longitude).append("\n");
        stringBuilder.append("dateTime:").append(dateTime).append("\n");

        // OkHttpClient 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 요청 생성
        Request request = new Request.Builder()
                .url("https://fighting.japaneast.cloudapp.azure.com:7000/send")
                .addHeader("phoneNumber", phoneNumber)
                .addHeader("latitude", String.valueOf(latitude))
                .addHeader("longitude", String.valueOf(longitude))
                .addHeader("dateTime", dateTime)
                .post(RequestBody.create(MediaType.parse("application/octet-stream"), imageBytes)) // 이미지 데이터 전송
                .build();

        Log.d("request", String.valueOf(request));

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                showErrorDialog("서버 요청 실패: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    showErrorDialog("서버 응답 실패: " + response.code());
//                } else {
//                    showSuccessDialog("데이터 전송 성공");
//                }
//            }
//        });
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    hideLoadingDialog();
                    showErrorDialog("서버 요청 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    hideLoadingDialog();
                    if (!response.isSuccessful()) {
                        showErrorDialog("서버 응답 실패: " + response.code());
                    } else {
                        showSuccessDialog("데이터 전송 성공");
                    }
                });
            }
        });


    }
    private AlertDialog progressDialog;


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
