package dk.seahawk.locator.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import dk.seahawk.locator.R;
import dk.seahawk.locator.data.local.PreferenceManager;
import dk.seahawk.locator.data.model.LocationRecord;

/**
 * Adapter for displaying location history in RecyclerView
 */
public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.LocationViewHolder> {

    private List<LocationRecord> locations = new ArrayList<>();
    private Context context;
    private PreferenceManager preferenceManager;
    private OnDeleteClickListener deleteClickListener;
    private OnOpenMapsClickListener openMapsClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position, LocationRecord record);
    }

    public interface OnOpenMapsClickListener {
        void onOpenMapsClick(LocationRecord record);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnOpenMapsClickListener(OnOpenMapsClickListener listener) {
        this.openMapsClickListener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        preferenceManager = new PreferenceManager(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_location_history, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationRecord record = locations.get(position);
        holder.bind(record, preferenceManager.getTemperatureUnit());

        // Set up delete button click listener
        holder.itemView.findViewById(R.id.button_delete).setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position, record);
            }
        });

        // Set up open maps button click listener
        holder.itemView.findViewById(R.id.button_open_maps).setOnClickListener(v -> {
            if (openMapsClickListener != null) {
                openMapsClickListener.onOpenMapsClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setLocations(List<LocationRecord> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView textGridLocator;
        private final TextView textCoordinates;
        private final TextView textTimestamp;
        private final TextView textAltitude;
        private final Chip chipCallSign;
        private final Chip chipWeather;

        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            textGridLocator = itemView.findViewById(R.id.item_grid_locator);
            textCoordinates = itemView.findViewById(R.id.item_coordinates);
            textTimestamp = itemView.findViewById(R.id.item_timestamp);
            textAltitude = itemView.findViewById(R.id.item_height);
            chipCallSign = itemView.findViewById(R.id.chip_call_sign);
            chipWeather = itemView.findViewById(R.id.item_weather);
        }

        void bind(LocationRecord record, PreferenceManager.TemperatureUnit temperatureUnit) {
            if (textGridLocator != null) {
                textGridLocator.setText(record.getGridLocator());
            }
            if (textCoordinates != null) {
                textCoordinates.setText(record.getFormattedCoordinates());
            }
            if (textTimestamp != null) {
                textTimestamp.setText(record.getFormattedTimestampUTC());
            }
            if (textAltitude != null) {
                textAltitude.setText(record.getFormattedAltitude());
            }

            // Bind call sign
            if (chipCallSign != null) {
                String callSign = record.getCallSign();
                if (callSign != null && !callSign.isEmpty()) {
                    chipCallSign.setText(callSign);
                    chipCallSign.setVisibility(View.VISIBLE);
                } else {
                    chipCallSign.setVisibility(View.GONE);
                }
            }

            // Bind weather
            if (chipWeather != null) {
                if (record.hasWeatherData()) {
                    chipWeather.setText(record.getWeatherData().getFormattedWeather(temperatureUnit));
                    chipWeather.setVisibility(View.VISIBLE);
                } else {
                    chipWeather.setVisibility(View.GONE);
                }
            }
        }
    }
}
