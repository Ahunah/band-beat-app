package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity implements BookingAdapter.BookingActionListener {

    private DBHelper db;
    private Session session;
    private BookingAdapter adapter;
    private RecyclerView rvBookings;
    private TextView tvNoBookings;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        db = new DBHelper(this);
        session = new Session(this);

        // Check if user is logged in
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvBookings = findViewById(R.id.rvBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        btnBack = findViewById(R.id.btnBack);


        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(this);
        rvBookings.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookings = db.getBookingsForUser(session.getUserId());

        if (bookings.isEmpty()) {
            tvNoBookings.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            tvNoBookings.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
            adapter.setData(bookings);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    @Override
    public void onCancel(Booking booking) {
        int rowsAffected = db.cancelBooking(booking.id, session.getUserId());
        if (rowsAffected > 0) {
            Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
            loadBookings();
        } else {
            Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
        }
    }
}