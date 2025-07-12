package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderIdRequest;
import com.fu.thinh_nguyen.qrfoodorder.data.model.PayOSResponse;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.CustomerMainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentGate extends AppCompatActivity {

    private ProgressBar loadingBar;
    private WebView webView;
    private int orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payos);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        webView = findViewById(R.id.webViewPayOS);
        webView.getSettings().setJavaScriptEnabled(true);
        loadingBar = findViewById(R.id.loadingBar);
        getPayOSUrl(orderId);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Toast.makeText(this, "Thoát khỏi thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        });


    }

    private void getPayOSUrl(int orderId) {
        loadingBar.setVisibility(View.VISIBLE);

        TokenManager tokenManager = new TokenManager(this);
        ApiService apiService = RetrofitClient.getInstance(tokenManager).create(ApiService.class);



        apiService.getPayOSCheckoutUrl(orderId).enqueue(new Callback<PayOSResponse>() {
            @Override
            public void onResponse(Call<PayOSResponse> call, Response<PayOSResponse> response) {
                loadingBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String checkoutUrl = response.body().getCheckoutUrl();
                    openPayOSWebview(checkoutUrl);
                } else {
                    Toast.makeText(PaymentGate.this, "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PayOSResponse> call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                Toast.makeText(PaymentGate.this, "Không thể kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openPayOSWebview(String checkoutUrl) {
        loadingBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingBar.setVisibility(View.GONE);

                if (url.contains("status=PAID")) {
                    Toast.makeText(PaymentGate.this, "Xác nhận thanh toán...", Toast.LENGTH_SHORT).show();

                    // Gọi API markAsPaid
                    TokenManager tokenManager = new TokenManager(PaymentGate.this);
                    OrderService orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

                    orderService.markOrderAsPaid(orderId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(PaymentGate.this, "Đã thanh toán thành công!", Toast.LENGTH_SHORT).show();
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PaymentGate.this, CustomerMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(PaymentGate.this, "Lỗi xác nhận thanh toán!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(PaymentGate.this, "Lỗi xác nhận thanh toán!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (url.contains("status=CANCELLED") || url.contains("status=FAILED")) {
                    Toast.makeText(PaymentGate.this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
                    showReturnScreen(); // Cho phép quay lại sau 1 phút
                }
            }


        });

        webView.loadUrl(checkoutUrl);
    }

    private void showReturnScreen() {
        runOnUiThread(() -> {
            Button btnBack = findViewById(R.id.btnBack);
            btnBack.setVisibility(View.VISIBLE);
        });



        new android.os.Handler().postDelayed(() -> {
            if (!isFinishing()) {
                Toast.makeText(PaymentGate.this, "Tự động thoát sau 1 phút", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 60000);
    }

}
