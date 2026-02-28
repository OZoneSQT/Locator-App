package dk.seahawk.locator.widget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dk.seahawk.locator.R;
import dk.seahawk.locator.business.algorithm.GridAlgorithm;
import dk.seahawk.locator.data.local.PreferenceManager;
import dk.seahawk.locator.ui.activity.MainActivity;

/**
 * Base widget provider for displaying current grid locator on home screen
 */
public abstract class LocationWidgetProvider extends AppWidgetProvider {

    protected static final String ACTION_UPDATE_WIDGET = "dk.seahawk.locator.ACTION_UPDATE_WIDGET";
    private static final int ALARM_ID = 1001;

    protected abstract int getLayoutResourceId();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        startAlarm(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        cancelAlarm(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isInteractive()) {
                return;
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            
            updateSpecificVariant(context, appWidgetManager, LocationWidgetProviderLight.class);
            updateSpecificVariant(context, appWidgetManager, LocationWidgetProviderDark.class);
            updateSpecificVariant(context, appWidgetManager, LocationWidgetProviderWhite.class);
        }
    }

    private void updateSpecificVariant(Context context, AppWidgetManager appWidgetManager, Class<?> cls) {
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, cls));
        for (int id : ids) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), getLayoutResourceId());

        // Display last known grid immediately from cache
        PreferenceManager pm = new PreferenceManager(context);
        views.setTextViewText(R.id.widget_grid_locator, pm.getLastGrid());

        // Set refresh button text programmatically to combine symbol and app name
        String refreshLabel = context.getString(R.string.widget_refresh_label, context.getString(R.string.app_name));
        views.setTextViewText(R.id.widget_refresh_button, refreshLabel);

        // Setup click handler to open app
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_grid_locator, pendingIntent);

        // Setup refresh button
        Intent refreshIntent = new Intent(context, this.getClass());
        refreshIntent.setAction(ACTION_UPDATE_WIDGET);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        fetchLocationAndUpdate(context, appWidgetManager, appWidgetId, views);
    }

    private void fetchLocationAndUpdate(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, RemoteViews views) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateWidgetWithLocation(context, appWidgetManager, appWidgetId, views, location);
                    } else {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLocation -> {
                            if (lastLocation != null) {
                                updateWidgetWithLocation(context, appWidgetManager, appWidgetId, views, lastLocation);
                            }
                        });
                    }
                });
    }

    private void updateWidgetWithLocation(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, RemoteViews views, Location location) {
        GridAlgorithm gridAlgorithm = new GridAlgorithm();
        String gridLocator = gridAlgorithm.getGridLocation(location);
        
        PreferenceManager pm = new PreferenceManager(context);
        pm.setLastGrid(gridLocator);
        
        views.setTextViewText(R.id.widget_grid_locator, gridLocator);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String updateTime = timeFormat.format(new Date());
        views.setTextViewText(R.id.widget_last_update, String.format(context.getString(R.string.widget_updated), updateTime));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(ACTION_UPDATE_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long interval = 60 * 1000; 
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 
                SystemClock.elapsedRealtime() + interval, interval, pendingIntent);
    }

    private void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(ACTION_UPDATE_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    public static void updateAllWidgets(Context context) {
        sendUpdateBroadcast(context, LocationWidgetProviderLight.class);
        sendUpdateBroadcast(context, LocationWidgetProviderDark.class);
        sendUpdateBroadcast(context, LocationWidgetProviderWhite.class);
    }

    private static void sendUpdateBroadcast(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.sendBroadcast(intent);
    }
}
