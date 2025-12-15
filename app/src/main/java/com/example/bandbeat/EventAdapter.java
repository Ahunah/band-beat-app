package com.example.bandbeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private final EventClickListener listener;
    private final boolean isAdmin;
    private final Context context;

    public EventAdapter(Context context, boolean isAdmin, EventClickListener listener) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    public void setData(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, isAdmin, listener);

        // Set event image
        int imageResource = getImageResource(event);
        holder.imgEvent.setImageResource(imageResource);
    }

    private int getImageResource(Event event) {
        if (event.imageName != null && !event.imageName.isEmpty()) {
            // Remove file extension if present and get resource ID
            String resourceName = event.imageName.replace(".png", "").replace(".jpg", "").replace(".xml", "");
            int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());

            // If found, return it
            if (resId != 0) {
                return resId;
            }
        }

        // Fallback to band-based images
        return getImageResourceByBand(event.band);
    }

    private int getImageResourceByBand(String bandName) {
        if (bandName == null) {
            return R.drawable.rock_concert;
        }

        String lowerBand = bandName.toLowerCase();

        if (lowerBand.contains("rock") || lowerBand.contains("metal")) {
            return R.drawable.rock_concert;
        } else if (lowerBand.contains("jazz") || lowerBand.contains("classical")) {
            return R.drawable.jazz_night;
        } else if (lowerBand.contains("pop")) {
            return R.drawable.pop_show;
        } else if (lowerBand.contains("indie")) {
            return R.drawable.indie_band;
        } else if (lowerBand.contains("hiphop") || lowerBand.contains("rap")) {
            return R.drawable.hiphop;
        } else if (lowerBand.contains("acoustic")) {
            return R.drawable.acoustic;
        } else if (lowerBand.contains("reggae")) {
            return R.drawable.reggae;
        } else {
            return R.drawable.rock_concert;
        }
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvBand;
        private final ImageView imgEvent;
        private final View btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBand = itemView.findViewById(R.id.tvBand);
            imgEvent = itemView.findViewById(R.id.imgEvent);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Event event, boolean isAdmin, EventClickListener listener) {
            tvName.setText(event.name);
            tvBand.setText(event.band);

            // Show/hide admin buttons
            if (isAdmin) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);

                btnEdit.setOnClickListener(v -> listener.onEventEdit(event));
                btnDelete.setOnClickListener(v -> listener.onEventDelete(event));
            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }

            // Item click for event details
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }

    public interface EventClickListener {
        void onEventClick(Event event);
        void onEventEdit(Event event);
        void onEventDelete(Event event);
    }
}