package com.example.payment_backend.service;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency}")
    private String currency;

    public Map<String, String> createOrder(int amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1); // Auto capture

        Order order = razorpay.orders.create(orderRequest);

        Map<String, String> response = new HashMap<>();
        response.put("order_id", order.get("id"));
        response.put("key", razorpayKeyId); // Send key to frontend
        return response;
    }

    public Map<String, String> verifyPayment(Map<String, String> paymentDetails) {
        try {
            String orderId = paymentDetails.get("razorpay_order_id");
            String paymentId = paymentDetails.get("razorpay_payment_id");
            String signature = paymentDetails.get("razorpay_signature");

            String generatedSignature = generateHMAC(orderId + "|" + paymentId, razorpayKeySecret);

            if (generatedSignature.equals(signature)) {
                return Map.of("status", "success", "message", "Payment verified successfully.");
            } else {
                return Map.of("status", "failure", "message", "Payment verification failed.");
            }
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error verifying payment: " + e.getMessage());
        }
    }

    private String generateHMAC(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
    }
}
