package com.example.bandbeat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    public interface BookingActionListener {
        void onCancel(Booking b);
    }

    private final BookingActionListener listener;
    private List<Booking> data = new ArrayList<>();

    public BookingAdapter(BookingActionListener l) {
        this.listener = l;
    }

    public void setData(List<Booking> list) {
        data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Booking b = data.get(pos);
        h.tvEvent.setText(b.eventSummary != null ? b.eventSummary : "Event details not available");
        h.tvQty.setText("Tickets: " + b.quantity);
        h.tvDate.setText("Booked on: " + formatDate(b.createdAt));
        h.btnCancel.setOnClickListener(v -> listener.onCancel(b));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String formatDate(String dateTime) {
        try {
            // Format: "2024-01-15 14:30:00" -> "Jan 15, 2024 at 14:30"
            if (dateTime != null && dateTime.length() >= 16) {
                String date = dateTime.substring(0, 10);
                String time = dateTime.substring(11, 16);
                return date + " at " + time;
            }
            return dateTime;
        } catch (Exception e) {
            return dateTime;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEvent, tvQty, tvDate;
        Button btnCancel;

        VH(@NonNull View v) {
            super(v);
            tvEvent = v.findViewById(R.id.tvEvent);
            tvQty = v.findViewById(R.id.tvQty);
            tvDate = v.findViewById(R.id.tvDate);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}