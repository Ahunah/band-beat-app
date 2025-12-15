package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private DBHelper db;
    private Session session;
    private Event event;
    private EditText etQty;
    private Button btnConfirm;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        db = new DBHelper(this);
        session = new Session(this);

        if (session.isAdmin()) {
            Toast.makeText(this, "Admins cannot book events", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int eventId = getIntent().getIntExtra("event_id", -1);
        event = db.getEventById(eventId);
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvEvent = findViewById(R.id.tvEvent);
        etQty = findViewById(R.id.etQty);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Set event details
        String eventDetails = event.name + "\n" +
                "Band   :  " + event.band + "\n" +
                "Date   :  " + event.date + " at " + event.time + "\n" +
                "Venue  :  " + event.venue + "\n" +
                "Price  :  LKR " + String.format("%.2f", event.price) + " per ticket";
        tvEvent.setText(eventDetails);

        btnConfirm.setOnClickListener(v -> confirmBooking());
        btnBack.setOnClickListener(v -> onBackPressed());

    }

    private void confirmBooking() {
        String quantityStr = etQty.getText().toString().trim();

        if (TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "Please enter number of tickets", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity < 1 || quantity > 10) {
            Toast.makeText(this, "Tickets must be between 1 and 10", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total price
        double totalPrice = event.price * quantity;

        // Create booking
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        long bookingId = db.addBooking(session.getUserId(), event.id, quantity, now);

        if (bookingId > 0) {
            String successMessage = "Booking confirmed!\n" +
                    quantity + " ticket(s) for " + event.name + "\n" +
                    "Total: LKR " + String.format("%.2f", totalPrice);
            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

            // Go back to event details or home
            finish();
        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}