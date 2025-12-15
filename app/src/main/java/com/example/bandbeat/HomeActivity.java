package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements EventAdapter.EventClickListener {

    private DBHelper db;
    private Session session;
    private EventAdapter adapter;
    private TextView tvNoEvents;
    private RecyclerView rvEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new DBHelper(this);
        session = new Session(this);

        // Check if user is logged in
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvEvents = findViewById(R.id.rvEvents);
        tvNoEvents = findViewById(R.id.tvNoEvents);

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this, session.isAdmin(), this);
        rvEvents.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            if (session.isAdmin()) {
                startActivity(new Intent(this, EventFormActivity.class));
            } else {
                Toast.makeText(this, "Only admins can add events", Toast.LENGTH_SHORT).show();
            }
        });

        // Show FAB only for admin
        fab.setVisibility(session.isAdmin() ? View.VISIBLE : View.GONE);

        ImageButton btnMyBookings = findViewById(R.id.btnMyBookings);
        btnMyBookings.setOnClickListener(v -> {
            if (!session.isAdmin()) {
                startActivity(new Intent(this, MyBookingsActivity.class));
            } else {
                Toast.makeText(this, "Admins don't have bookings", Toast.LENGTH_SHORT).show();
            }
        });

        // Hide bookings button for admin
        btnMyBookings.setVisibility(session.isAdmin() ? View.GONE : View.VISIBLE);

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        refreshEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEvents();
    }

    private void refreshEvents() {
        List<Event> events = db.getAllEvents();
        adapter.setData(events);

        if (events.isEmpty()) {
            if (tvNoEvents != null) {
                tvNoEvents.setVisibility(View.VISIBLE);
                rvEvents.setVisibility(View.GONE);
            }
            if (session.isAdmin()) {
                Toast.makeText(this, "No events available. Tap + to add events.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No events available yet.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (tvNoEvents != null) {
                tvNoEvents.setVisibility(View.GONE);
                rvEvents.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.id);
        startActivity(intent);
    }

    @Override
    public void onEventEdit(Event event) {
        if (session.isAdmin()) {
            Intent intent = new Intent(this, EventFormActivity.class);
            intent.putExtra("event_id", event.id);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Only admins can edit events", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventDelete(Event event) {
        if (session.isAdmin()) {
            int rowsAffected = db.deleteEvent(event.id);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                refreshEvents();
            } else {
                Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Only admins can delete events", Toast.LENGTH_SHORT).show();
        }
    }
}