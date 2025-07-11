package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;
    private int orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        webView = findViewById(R.id.webViewVNPay);

        webView.getSettings().setJavaScriptEnabled(true);

        // 1. Gọi API từ server để lấy URL thanh toán
        getVNPayUrl(orderId);
    }

    private void getVNPayUrl(int orderId) {
        // Gọi API backend để tạo URL thanh toán
        // Ví dụ: http://yourserver.com/api/payment/vnpay?orderId=123

        String url = "http://yourserver.com/api/payment/vnpay?orderId=" + orderId;

        // 2. Load URL vào WebView
        webView.loadUrl(url);

        // 3. Lắng nghe URL callback thành công/thất bại
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("vnp_ResponseCode=00")) {
                    Toast.makeText(VNPayActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    finish(); // hoặc điều hướng về trang chính
                    return true;
                } else if (url.contains("vnp_ResponseCode")) {
                    Toast.makeText(VNPayActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }

                return false;
            }
        });
    }
}

