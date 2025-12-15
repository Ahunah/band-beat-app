package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class EventDetailActivity extends AppCompatActivity {
    private DBHelper db;
    private Session session;
    private Event event;

    private ImageView btnBack; // Add this

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        db = new DBHelper(this);
        session = new Session(this);

        int eventId = getIntent().getIntExtra("event_id", -1);
        event = db.getEventById(eventId);
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tvName);
        TextView tvBand = findViewById(R.id.tvBand);
        TextView tvDT = findViewById(R.id.tvDateTime);
        TextView tvVenue = findViewById(R.id.tvVenue);
        TextView tvPrice = findViewById(R.id.tvPrice);
        MaterialButton btnBook = findViewById(R.id.btnBook);
        MaterialButton btnEdit = findViewById(R.id.btnEdit);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);


        tvName.setText(event.name);
        tvBand.setText("Band   :    " + event.band);
        tvDT.setText("Date & Time  : " + event.date + " • " + event.time);
        tvVenue.setText("Venue  :    " + event.venue);
        tvPrice.setText("Price  :  LKR " + String.format("%.2f", event.price));

        boolean isAdmin = session.isAdmin();

        // Show book button only for users
        btnBook.setVisibility(isAdmin ? View.GONE : View.VISIBLE);

        // Show edit/delete buttons only for admin
        btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        btnBook.setOnClickListener(v -> {
            Intent i = new Intent(this, BookingActivity.class);
            i.putExtra("event_id", event.id);
            startActivity(i);
        });

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EventFormActivity.class);
            i.putExtra("event_id", event.id);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            int rowsAffected = db.deleteEvent(event.id);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());

    }
}