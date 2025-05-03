package com.example.secretcalculator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText etNumber1, etNumber2;
    private TextView tvResult;
    private Button btnAdd, btnSubtract, btnMultiply, btnDivide, btnCalculate;
    private ImageView ivImage;
    private String operation = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_LOCATION = 2;
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private FusedLocationProviderClient fusedLocationClient;
    private final int[] nimDigits = {0, 2, 8, 9}; // 4 digit terakhir NIM, digit 5 (0) digantikan oleh 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen UI
        etNumber1 = findViewById(R.id.etNumber1);
        etNumber2 = findViewById(R.id.etNumber2);
        tvResult = findViewById(R.id.tvResult);
        btnAdd = findViewById(R.id.btnAdd);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnDivide = findViewById(R.id.btnDivide);
        btnCalculate = findViewById(R.id.btnCalculate);
        ivImage = findViewById(R.id.ivImage);

        // Inisialisasi sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Inisialisasi lokasi
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            Toast.makeText(this, "Gagal menginisialisasi layanan lokasi", Toast.LENGTH_SHORT).show();
        }

        // Set operasi
        btnAdd.setOnClickListener(v -> operation = "+");
        btnSubtract.setOnClickListener(v -> operation = "-");
        btnMultiply.setOnClickListener(v -> operation = "*");
        btnDivide.setOnClickListener(v -> operation = "/");

        // Hitung dan cek hasil
        btnCalculate.setOnClickListener(v -> calculate());
    }

    private void calculate() {
        String num1Str = etNumber1.getText().toString();
        String num2Str = etNumber2.getText().toString();

        if (num1Str.isEmpty() || num2Str.isEmpty() || operation.isEmpty()) {
            Toast.makeText(this, "Masukkan angka dan pilih operasi", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double num1 = Double.parseDouble(num1Str);
            double num2 = Double.parseDouble(num2Str);
            double result = 0;

            switch (operation) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        Toast.makeText(this, "Tidak dapat membagi dengan nol", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    result = num1 / num2;
                    break;
            }

            tvResult.setText("Hasil: " + result);
            checkResult((int) Math.floor(result));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan angka yang valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkResult(int result) {
        // Reset ImageView ke gone sebelum memeriksa hasil baru
        ivImage.setVisibility(View.GONE);

        // Periksa hasil 1 untuk Gyroscope dan Accelerometer
        if (result == 1) {
            showSensors();
            return;
        }

        // Periksa digit NIM
        for (int i = 0; i < nimDigits.length; i++) {
            if (result == nimDigits[i]) {
                switch (i) {
                    case 0: // Digit 1: Kamera (0)
                        openCamera();
                        break;
                    case 1: // Digit 2: Audio (2)
                        playAudio();
                        break;
                    case 2: // Digit 3: Gambar (8)
                        showImage();
                        break;
                    case 3: // Digit 4: GPS (9)
                        showLocation();
                        break;
                }
                return;
            }
        }
        Toast.makeText(this, "Hasil tidak cocok dengan digit NIM atau angka 1", Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        // Periksa izin kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        // Periksa apakah perangkat memiliki fitur kamera
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "Perangkat tidak mendukung kamera", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat Intent untuk membuka kamera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                Log.d("SecretCalculator", "Membuka aplikasi kamera");
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "Tidak ada aplikasi kamera yang tersedia. Silakan instal aplikasi kamera.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("SecretCalculator", "Gagal membuka kamera: " + e.getMessage());
            Toast.makeText(this, "Gagal membuka kamera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mp.release());
        } else {
            Toast.makeText(this, "Gagal memutar audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image);
        if (bitmap != null) {
            ivImage.setImageBitmap(bitmap);
            ivImage.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLocation() {
        if (fusedLocationClient == null) {
            Toast.makeText(this, "Layanan lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Periksa apakah GPS aktif
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS tidak aktif. Silakan aktifkan GPS.", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            String loc = "Latitude: " + location.getLatitude() +
                                    ", Longitude: " + location.getLongitude();
                            Toast.makeText(this, loc, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showSensors() {
        if (accelerometer != null && gyroscope != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Sensor diaktifkan, lihat log", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sensor tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            String data = "Accelerometer: X=" + event.values[0] + ", Y=" + event.values[1] +
                    ", Z=" + event.values[2];
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            String data = "Gyroscope: X=" + event.values[0] + ", Y=" + event.values[1] +
                    ", Z=" + event.values[2];
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        }
        // Hentikan listener setelah menampilkan data
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Tidak diperlukan
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == REQUEST_LOCATION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLocation();
        } else {
            Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}