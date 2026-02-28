package dk.seahawk.locator.algorithm;

import org.junit.jupiter.api.Test;

import muckup.MuckUpLocation;
import dk.seahawk.locator.business.algorithm.GridAlgorithm;
import dk.seahawk.locator.business.algorithm.GridAlgorithmInterface;

import static org.junit.jupiter.api.Assertions.*;

class GridAlgorithmInterfaceTest {
    private MuckUpLocation muckUpLocation;
    private GridAlgorithmInterface gridAlgorithmInterface;

    @Test   // Signal Hill, Cape Town - https://www.karhukoti.com/maidenhead-grid-square-locator/?grid=JF96eb
    void getGridLocation() {
        double longitude = 18.388544;
        double latitude = -33.934562;
        double altitude = 57.3;
        float accuracy = 0.125f;

        muckUpLocation = new MuckUpLocation(longitude, latitude, altitude, accuracy);
        gridAlgorithmInterface = new GridAlgorithm();

        assertEquals("JF96EB", gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocation));
    }

    @Test // My QTH - https://www.karhukoti.com/maidenhead-grid-square-locator/?grid=JO45sv
    void getGridLocationA() {
        double longitude = 9.563663;
        double latitude = 55.887282;
        double altitude = 57.3;
        float accuracy = 0.125f;

        muckUpLocation = new MuckUpLocation(longitude, latitude, altitude, accuracy);
        gridAlgorithmInterface = new GridAlgorithm();

        assertEquals("JO45SV", gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocation));
    }

    @Test   // VIA, center of E - https://www.karhukoti.com/maidenhead-grid-square-locator/?grid=JO45wu
    void getGridLocationB() {
        double longitude = 9.8860909;
        double latitude = 55.8719648;
        double altitude = 63.2;
        float accuracy = 0.513f;

        muckUpLocation = new MuckUpLocation(longitude, latitude, altitude, accuracy);
        gridAlgorithmInterface = new GridAlgorithm();

        assertEquals("JO45WU", gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocation));
    }

    @Test   // Qasigiannguit - https://www.karhukoti.com/maidenhead-grid-square-locator/?grid=GP48jt
    void getGridLocationC() {
        double longitude = -51.1967887;
        double latitude = 68.8197614;
        double altitude = 26.8;
        float accuracy = 0.213f;

        muckUpLocation = new MuckUpLocation(longitude, latitude, altitude, accuracy);
        gridAlgorithmInterface = new GridAlgorithm();

        assertEquals("GP48JT", gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocation));
    }
}
