package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.TableService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 1001;
    private PreviewView previewView;
    private Executor executor;

    private boolean isProcessing = false;
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr); // RẤT QUAN TRỌNG

        previewView = findViewById(R.id.previewView);
        executor = ContextCompat.getMainExecutor(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        Button btnCancel = findViewById(R.id.btn_cancel_scan);
        btnCancel.setOnClickListener(v -> finish());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(executor, imageProxy -> {
                    if (isProcessing) {
                        imageProxy.close();
                        return;
                    }

                    @SuppressWarnings("UnsafeOptInUsageError")
                    Image mediaImage = imageProxy.getImage();

                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.getImageInfo().getRotationDegrees());

                        BarcodeScanner scanner = BarcodeScanning.getClient();
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String rawValue = barcode.getRawValue();
                                        if (rawValue != null) {
                                            isProcessing = true; // << CHẶN các lần gọi sau
                                            imageProxy.close();
                                            callGetTableByQrCode(rawValue);
                                            return;
                                        }
                                    }
                                    imageProxy.close();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("QR_SCAN", "Scan failed", e);
                                    imageProxy.close();
                                });
                    } else {
                        imageProxy.close();
                    }
                });


                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    // Xử lý kết quả xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void callGetTableByQrCode(String qrCode) {
        TokenManager tokenManager = new TokenManager(this);
        TableService service = RetrofitClient.getInstance(tokenManager).create(TableService.class);

        service.getTableByQrCode(qrCode).enqueue(new Callback<TableDto>() {

            @Override
            public void onResponse(Call<TableDto> call, Response<TableDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RAW", "Status code: " + response.code());
                    Log.d("API_RAW", "Error body: " + response.errorBody());
                    Log.d("API_RAW", "Body is null? " + (response.body() == null));

                    TableDto table = response.body();
                    goToTableStatusScreen(table);
                } else {
                    Toast.makeText(ScanQRActivity.this,
                            "Mã QR không khớp với bàn nào trong hệ thống",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TableDto> call, Throwable t) {
                Toast.makeText(ScanQRActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.d("API_RAW", "Error body: " + t.getMessage());
            }
        });
    }


    private void goToTableStatusScreen(TableDto table) {
        Intent intent = new Intent(this, QrTableStatusActivity.class);
        intent.putExtra("TABLE_ID", table.getId());
        intent.putExtra("TABLE_NAME", table.getTableNumber());
        intent.putExtra("TABLE_STATUS", table.getStatus());
        startActivity(intent);
        finish();
    }


}
