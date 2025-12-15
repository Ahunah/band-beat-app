package com.example.bandbeat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class EventFormActivity extends AppCompatActivity {

    private EditText etName, etBand, etDate, etTime, etVenue, etPrice;
    private Spinner spinnerImage;
    private Button btnSave;
    private DBHelper db;
    private Session session;
    private Event existingEvent;
    private ImageView btnBack;


    // Map image names to display names
    private final Map<String, String> imageMap = new HashMap<String, String>() {{
        put("rock_concert", "Rock Concert");
        put("jazz_night", "Jazz Night");
        put("pop_show", "Pop Show");
        put("indie_band", "Indie Band");
        put("hiphop", "Hip Hop");
        put("acoustic", "Acoustic");
        put("reggae", "Reggae");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        db = new DBHelper(this);
        session = new Session(this);

        if (!session.isLoggedIn() || !session.isAdmin()) {
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());


        initViews();

        // Check if editing existing event
        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) {
            existingEvent = db.getEventById(eventId);
            if (existingEvent != null) {
                populateForm(existingEvent);
            }
        }

        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etBand = findViewById(R.id.etBand);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etVenue = findViewById(R.id.etVenue);
        etPrice = findViewById(R.id.etPrice);
        spinnerImage = findViewById(R.id.spinnerImage);
        btnSave = findViewById(R.id.btnSave);

        // Setup image spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                imageMap.values().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImage.setAdapter(adapter);
    }

    private void populateForm(Event event) {
        etName.setText(event.name);
        etBand.setText(event.band);
        etDate.setText(event.date);
        etTime.setText(event.time);
        etVenue.setText(event.venue);
        etPrice.setText(String.valueOf(event.price));

        // Set selected image
        if (event.imageName != null && imageMap.containsKey(event.imageName)) {
            String displayName = imageMap.get(event.imageName);
            ArrayAdapter adapter = (ArrayAdapter) spinnerImage.getAdapter();
            int position = adapter.getPosition(displayName);
            if (position >= 0) {
                spinnerImage.setSelection(position);
            }
        }
    }

    private void saveEvent() {
        String name = etName.getText().toString().trim();
        String band = etBand.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || band.isEmpty() || date.isEmpty() ||
                time.isEmpty() || venue.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected image
        String selectedDisplayName = spinnerImage.getSelectedItem().toString();
        String imageName = getKeyFromValue(imageMap, selectedDisplayName);

        Event event = new Event();
        event.name = name;
        event.band = band;
        event.date = date;
        event.time = time;
        event.venue = venue;
        event.price = price;
        event.createdBy = session.getUserId();
        event.imageName = imageName;

        boolean success;
        if (existingEvent != null) {
            event.id = existingEvent.id;
            success = db.updateEvent(event) > 0;
        } else {
            success = db.addEvent(event) > 0;
        }

        if (success) {
            Toast.makeText(this,
                    existingEvent != null ? "Event updated!" : "Event created!",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
        }
    }

    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return "rock_concert"; // default
    }
}