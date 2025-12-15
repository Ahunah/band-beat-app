package com.example.bandbeat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "bandbeat.db";
    public static final int DB_VERSION = 3; // Incremented version

    // Users
    public static final String T_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_NAME = "name";
    public static final String U_EMAIL = "email";
    public static final String U_PASSWORD = "password";
    public static final String U_ROLE = "role";

    // Events
    public static final String T_EVENTS = "events";
    public static final String E_ID = "id";
    public static final String E_NAME = "name";
    public static final String E_BAND = "band";
    public static final String E_DATE = "date";
    public static final String E_TIME = "time";
    public static final String E_VENUE = "venue";
    public static final String E_PRICE = "price";
    public static final String E_CREATED_BY = "created_by";
    public static final String E_IMAGE_NAME = "image_name"; // New column

    // Bookings
    public static final String T_BOOKINGS = "bookings";
    public static final String B_ID = "id";
    public static final String B_USER_ID = "user_id";
    public static final String B_EVENT_ID = "event_id";
    public static final String B_QTY = "quantity";
    public static final String B_CREATED_AT = "created_at";

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Users table
            db.execSQL("CREATE TABLE " + T_USERS + " (" +
                    U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    U_NAME + " TEXT NOT NULL," +
                    U_EMAIL + " TEXT UNIQUE NOT NULL," +
                    U_PASSWORD + " TEXT NOT NULL," +
                    U_ROLE + " TEXT NOT NULL" +
                    ");");

            // Events table with image_name
            db.execSQL("CREATE TABLE " + T_EVENTS + " (" +
                    E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    E_NAME + " TEXT NOT NULL," +
                    E_BAND + " TEXT NOT NULL," +
                    E_DATE + " TEXT NOT NULL," +
                    E_TIME + " TEXT NOT NULL," +
                    E_VENUE + " TEXT NOT NULL," +
                    E_PRICE + " REAL NOT NULL," +
                    E_CREATED_BY + " INTEGER," +
                    E_IMAGE_NAME + " TEXT" +
                    ");");

            // Bookings table
            db.execSQL("CREATE TABLE " + T_BOOKINGS + " (" +
                    B_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    B_USER_ID + " INTEGER NOT NULL," +
                    B_EVENT_ID + " INTEGER NOT NULL," +
                    B_QTY + " INTEGER NOT NULL," +
                    B_CREATED_AT + " TEXT NOT NULL" +
                    ");");

            // Seed default admin
            ContentValues admin = new ContentValues();
            admin.put(U_NAME, "Administrator");
            admin.put(U_EMAIL, "admin@bandbeat.com");
            admin.put(U_PASSWORD, "admin123");
            admin.put(U_ROLE, "ADMIN");
            db.insert(T_USERS, null, admin);

            // Seed some sample events with images
            seedSampleEvents(db);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedSampleEvents(SQLiteDatabase db) {
        // Sample event 1 - Rock Concert
        ContentValues event1 = new ContentValues();
        event1.put(E_NAME, "Rock Festival 2024");
        event1.put(E_BAND, "The Rock Legends");
        event1.put(E_DATE, "2024-12-15");
        event1.put(E_TIME, "19:00");
        event1.put(E_VENUE, "Central Stadium");
        event1.put(E_PRICE, 75.0);
        event1.put(E_CREATED_BY, 1);
        event1.put(E_IMAGE_NAME, "rock_concert");
        db.insert(T_EVENTS, null, event1);

        // Sample event 2 - Jazz Night
        ContentValues event2 = new ContentValues();
        event2.put(E_NAME, "Smooth Jazz Evening");
        event2.put(E_BAND, "Jazz Quartet");
        event2.put(E_DATE, "2024-12-10");
        event2.put(E_TIME, "20:30");
        event2.put(E_VENUE, "Blue Note Club");
        event2.put(E_PRICE, 45.0);
        event2.put(E_CREATED_BY, 1);
        event2.put(E_IMAGE_NAME, "jazz_night");
        db.insert(T_EVENTS, null, event2);

        // Sample event 3 - Hip Hop Show
        ContentValues event3 = new ContentValues();
        event3.put(E_NAME, "Urban Beats Night");
        event3.put(E_BAND, "Hip Hop Collective");
        event3.put(E_DATE, "2024-12-20");
        event3.put(E_TIME, "21:00");
        event3.put(E_VENUE, "City Arena");
        event3.put(E_PRICE, 60.0);
        event3.put(E_CREATED_BY, 1);
        event3.put(E_IMAGE_NAME, "hiphop");
        db.insert(T_EVENTS, null, event3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Add image_name column if upgrading from older version
            db.execSQL("ALTER TABLE " + T_EVENTS + " ADD COLUMN " + E_IMAGE_NAME + " TEXT");
            // Re-seed sample events with images
            seedSampleEvents(db);
        }
    }

    /* =================== USERS =================== */

    public long registerUser(String name, String email, String password) {
        ContentValues cv = new ContentValues();
        cv.put(U_NAME, name);
        cv.put(U_EMAIL, email);
        cv.put(U_PASSWORD, password);
        cv.put(U_ROLE, "USER");
        return getWritableDatabase().insert(T_USERS, null, cv);
    }

    public int login(String email, String password, String expectedRole) {
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    T_USERS, null,
                    U_EMAIL + "=? AND " + U_PASSWORD + "=? AND " + U_ROLE + "=?",
                    new String[]{email, password, expectedRole}, null, null, null
            );

            if (c != null && c.moveToFirst()) {
                int id = c.getInt(c.getColumnIndexOrThrow(U_ID));
                c.close();
                return id;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        } finally {
            if (c != null) c.close();
        }
    }

    /* =================== EVENTS =================== */

    public long addEvent(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(E_NAME, e.name);
        cv.put(E_BAND, e.band);
        cv.put(E_DATE, e.date);
        cv.put(E_TIME, e.time);
        cv.put(E_VENUE, e.venue);
        cv.put(E_PRICE, e.price);
        cv.put(E_CREATED_BY, e.createdBy);
        cv.put(E_IMAGE_NAME, e.imageName);
        return getWritableDatabase().insert(T_EVENTS, null, cv);
    }

    public int updateEvent(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(E_NAME, e.name);
        cv.put(E_BAND, e.band);
        cv.put(E_DATE, e.date);
        cv.put(E_TIME, e.time);
        cv.put(E_VENUE, e.venue);
        cv.put(E_PRICE, e.price);
        cv.put(E_IMAGE_NAME, e.imageName);
        return getWritableDatabase().update(T_EVENTS, cv, E_ID + "=?", new String[]{String.valueOf(e.id)});
    }

    public int deleteEvent(int eventId) {
        return getWritableDatabase().delete(T_EVENTS, E_ID + "=?", new String[]{String.valueOf(eventId)});
    }

    public List<Event> getAllEvents() {
        ArrayList<Event> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(T_EVENTS, null, null, null, null, null, E_DATE + ", " + E_TIME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(eventFromCursor(c));
            }
            c.close();
        }
        return list;
    }

    public Event getEventById(int id) {
        Cursor c = getReadableDatabase().query(T_EVENTS, null, E_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Event e = eventFromCursor(c);
            c.close();
            return e;
        }
        if (c != null) c.close();
        return null;
    }

    private Event eventFromCursor(Cursor c) {
        Event e = new Event();
        e.id = c.getInt(c.getColumnIndexOrThrow(E_ID));
        e.name = c.getString(c.getColumnIndexOrThrow(E_NAME));
        e.band = c.getString(c.getColumnIndexOrThrow(E_BAND));
        e.date = c.getString(c.getColumnIndexOrThrow(E_DATE));
        e.time = c.getString(c.getColumnIndexOrThrow(E_TIME));
        e.venue = c.getString(c.getColumnIndexOrThrow(E_VENUE));
        e.price = c.getDouble(c.getColumnIndexOrThrow(E_PRICE));
        e.createdBy = c.getInt(c.getColumnIndexOrThrow(E_CREATED_BY));

        // Handle image name - use band-based default if not specified
        int imageNameIndex = c.getColumnIndex(E_IMAGE_NAME);
        if (imageNameIndex != -1) {
            e.imageName = c.getString(imageNameIndex);
        }
        if (e.imageName == null || e.imageName.isEmpty()) {
            e.imageName = getDefaultImageForBand(e.band);
        }

        return e;
    }

    private String getDefaultImageForBand(String band) {
        if (band == null) return "rock_concert";

        String lowerBand = band.toLowerCase();
        if (lowerBand.contains("rock") || lowerBand.contains("metal")) {
            return "rock_concert";
        } else if (lowerBand.contains("jazz") || lowerBand.contains("classical")) {
            return "jazz_night";
        } else if (lowerBand.contains("pop")) {
            return "pop_show";
        } else if (lowerBand.contains("indie")) {
            return "indie_band";
        } else if (lowerBand.contains("hiphop") || lowerBand.contains("rap")) {
            return "hiphop";
        } else if (lowerBand.contains("acoustic")) {
            return "acoustic";
        } else if (lowerBand.contains("reggae")) {
            return "reggae";
        } else {
            return "rock_concert";
        }
    }

    /* =================== BOOKINGS =================== */

    public long addBooking(int userId, int eventId, int qty, String createdAtIso) {
        ContentValues cv = new ContentValues();
        cv.put(B_USER_ID, userId);
        cv.put(B_EVENT_ID, eventId);
        cv.put(B_QTY, qty);
        cv.put(B_CREATED_AT, createdAtIso);
        return getWritableDatabase().insert(T_BOOKINGS, null, cv);
    }

    public int cancelBooking(int bookingId, int userId) {
        return getWritableDatabase().delete(T_BOOKINGS,
                B_ID + "=? AND " + B_USER_ID + "=?",
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
    }

    public List<Booking> getBookingsForUser(int userId) {
        ArrayList<Booking> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(T_BOOKINGS, null, B_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, B_CREATED_AT + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                Booking b = new Booking();
                b.id = c.getInt(c.getColumnIndexOrThrow(B_ID));
                b.userId = c.getInt(c.getColumnIndexOrThrow(B_USER_ID));
                b.eventId = c.getInt(c.getColumnIndexOrThrow(B_EVENT_ID));
                b.quantity = c.getInt(c.getColumnIndexOrThrow(B_QTY));
                b.createdAt = c.getString(c.getColumnIndexOrThrow(B_CREATED_AT));
                // attach event summary for display
                Event e = getEventById(b.eventId);
                if (e != null) b.eventSummary = e.name + " • " + e.band + " @ " + e.venue + " " + e.date + " " + e.time;
                list.add(b);
            }
            c.close();
        }
        return list;
    }
}