SecretCalculator
Aplikasi kalkulator Android dengan fitur tambahan: Kamera, Audio, Gambar, GPS, dan Sensor, berdasarkan 5 digit terakhir NIM (0, 2, 8, 9, 0). Hasil 1 memicu Gyroscope dan Accelerometer.
Prasyarat

Android Studio (versi terbaru direkomendasikan).
Git terinstal.
Perangkat fisik dengan aplikasi kamera bawaan atau emulator dengan kamera dikonfigurasi.
File sumber daya:
res/raw/sample_audio.mp3
res/drawable/sample_image.jpg
res/mipmap/ic_launcher.png (ikon kalkulator)



Cara Menjalankan

Klon repositori:git clone https://github.com/username/SecretCalculator.git


Buka proyek di Android Studio.
Sinkronkan proyek dengan Gradle (File > Sync Project with Gradle Files).
Tambahkan file sumber daya (sample_audio.mp3, sample_image.jpg, ikon kalkulator) ke direktori yang sesuai.
Jalankan aplikasi di perangkat fisik atau emulator dengan Google Play Services.
Uji fitur:
0 + 0 = 0: Membuka kamera.
1 + 0 = 1: Menampilkan data Gyroscope dan Accelerometer.
2 + 0 = 2: Memutar audio.
8 + 0 = 8: Menampilkan gambar.
9 + 0 = 9: Menampilkan lokasi GPS.



Catatan untuk Fitur Kamera
Jika muncul pesan "Tidak ada aplikasi kamera yang tersedia":

Perangkat Fisik: Pastikan aplikasi kamera bawaan tersedia dan izin kamera diaktifkan.
Emulator:
Gunakan image Google Play (API 30 atau lebih baru).
Aktifkan kamera di Extended Controls > Camera (atur ke Webcam0 atau Emulated).
Instal aplikasi kamera seperti Open Camera dari Play Store.



Dependensi

Gradle: com.google.android.gms:play-services-location:21.0.1
SDK: minSdk 21, targetSdk 34

Kontributor

Alfin Febrianto (NIM: STI202202890)

