package dk.seahawk.locator.widget;

import dk.seahawk.locator.R;

/**
 * Widget provider for the Light (Dark Text) version of the widget
 */
public class LocationWidgetProviderLight extends LocationWidgetProvider {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.location_widget_light;
    }
}
