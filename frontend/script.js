document.getElementById("payBtn").addEventListener("click", async function (e) {
  const name = document.getElementById("name").value;
  const company = document.getElementById("company").value;
  const email = document.getElementById("email").value;
  const phone = document.getElementById("phone").value;
  const jobTitle = document.getElementById("job").value;
  const commodity = document.getElementById("commodity").value;
  const country = document.getElementById("country").value;
  const state = document.getElementById("state").value;
  const city = document.getElementById("city").value;

  if (
    !name ||
    !company ||
    !email ||
    !phone ||
    !jobTitle ||
    !commodity ||
    !country ||
    !state ||
    !city
  ) {
    alert("Please fill all the required fields!");
    return;
  }

  try {
    // 1️⃣ Request Backend to Create Order
    const response = await fetch(
      "http://localhost:8080/api/payment/create-order",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount: 50000 }), // ₹500
      }
    );

    if (!response.ok) {
      throw new Error("Failed to create Razorpay order. Check backend.");
    }

    const orderData = await response.json();
    if (orderData.error) {
      alert(orderData.error);
      return;
    }

    const orderId = orderData.order_id;
    const razorpayKey = orderData.key; // Fetch Razorpay Key from backend

    // 2️⃣ Razorpay Checkout Options
    const options = {
      key: razorpayKey, // ✅ Razorpay Key from backend
      amount: 50000, // ₹500 in paise
      currency: "INR",
      name: company,
      description: "Registration Payment",
      order_id: orderId, // ✅ Order ID from backend
      handler: async function (response) {
        alert(
          "Payment Successful! Payment ID: " + response.razorpay_payment_id
        );

        // 3️⃣ Send Payment Details to Backend for Verification
        const verifyResponse = await fetch(
          "http://localhost:8080/api/payment/verify",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature,
            }),
          }
        );

        const verifyMessage = await verifyResponse.json();
        alert(verifyMessage.message); // Show backend verification message
      },
      prefill: { name: name, email: email, contact: phone },
      theme: { color: "#1e3c72" },
    };

    const rzp = new Razorpay(options);
    rzp.open();
  } catch (error) {
    console.error("Payment Error:", error);
    alert("Error while processing payment! Check console.");
  }
});
