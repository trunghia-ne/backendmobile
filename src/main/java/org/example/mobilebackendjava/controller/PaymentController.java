package org.example.mobilebackendjava.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> createPayment(@RequestParam int amount,
                                                             @RequestParam String userId,
                                                             HttpServletRequest request) {
        System.out.println("Backend received payment request, amount: " + amount + "++++ID:" + userId);

        // Tạo mã giao dịch gồm UUID + userId, cách nhau bởi dấu '-'
        String vnp_TxnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "-" + userId;

        String vnp_OrderInfo = "Thanh toan don hang " + vnp_TxnRef;
        String orderType = "other";

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_CurrCode = "VND";
        int amountInVND = amount * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", getClientIpAddr(request));

        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Sắp xếp và mã hóa các tham số
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
                query.append(fieldName).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
            }
        }

        // Bỏ ký tự '&' cuối
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // Tạo chữ ký và URL thanh toán
        String secureHash = HmacUtil.hmacSHA512(vnp_HashSecret, hashData.toString());
        String paymentUrl = vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        Map<String, String> result = new HashMap<>();
        result.put("paymentUrl", paymentUrl);
        result.put("transactionId", vnp_TxnRef);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vnpay-return")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String param = params.nextElement();
            if (param.startsWith("vnp_")) {
                fields.put(param, request.getParameter(param));
            }
        }

        // Lấy và loại bỏ các trường không cần thiết trước khi tạo chữ ký
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = HmacUtil.hmacSHA512(vnp_HashSecret, buildHashData(fields));

        String redirectUrl;
        if (signValue.equalsIgnoreCase(vnp_SecureHash)) { // So sánh không phân biệt hoa thường
            String responseCode = fields.get("vnp_ResponseCode");

            String transactionIdWithUser = fields.get("vnp_TxnRef");
            String[] parts = transactionIdWithUser.split("-", 2);
            String transactionId = parts[0];
            String userId = parts.length > 1 ? parts[1] : null;

            int amountInVND = Integer.parseInt(fields.get("vnp_Amount")) / 100;
            System.out.println("RECEIVED SecureHash: " + vnp_SecureHash);
            System.out.println("LOCAL Computed Hash: " + signValue);


            try {
                if ("00".equals(responseCode)) {
                    savePaymentToFirestore(transactionId, amountInVND, true, "VNpay", userId);
                    redirectUrl = "http://10.0.2.2:8080/payment-success";

                } else {
                    redirectUrl = "http://10.0.2.2:8080/payment-failed";
                }
            } catch (Exception e) {
                e.printStackTrace();
                redirectUrl = "http://10.0.2.2:8080/payment-error";
            }
        } else {
            redirectUrl = "http://10.0.2.2:8080/payment-invalid";
        }

        response.sendRedirect(redirectUrl);
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String field : fieldNames) {
            hashData.append(field).append('=')
                    .append(URLEncoder.encode(params.get(field), StandardCharsets.UTF_8))
                    .append('&');
        }
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        return hashData.toString();
    }




    private void savePaymentToFirestore(String transactionId, int amount, boolean paid, String paymentMethod, String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("transactionId", transactionId);
        data.put("amount", amount);
        data.put("paid", paid);
        data.put("paymentMethod", paymentMethod);
        data.put("paymentTime", new Date());
        data.put("userId", userId);

        ApiFuture<DocumentReference> result = db.collection("payments").add(data);
        System.out.println("Added payment with ID: " + result.get().getId());
    }


    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
