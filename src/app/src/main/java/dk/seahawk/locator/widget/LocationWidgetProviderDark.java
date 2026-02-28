package dk.seahawk.locator.widget;

import dk.seahawk.locator.R;

/**
 * Widget provider for the Dark (Light Text) version of the widget
 */
public class LocationWidgetProviderDark extends LocationWidgetProvider {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.location_widget_dark;
    }
}
