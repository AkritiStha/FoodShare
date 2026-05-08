package service;

import util.DistanceCalculator;

/**
 * Service wrapper around DistanceCalculator.
 * Provides distance calculations for location-based food matching.
 */
public class LocationService {

    /**
     * Calculates the great-circle distance (km) between two coordinates
     * using the Haversine formula.
     */
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        return DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
    }

    /**
     * Validates that a latitude value is in the valid range [-90, 90].
     */
    public boolean isValidLatitude(double lat) {
        return lat >= -90.0 && lat <= 90.0;
    }

    /**
     * Validates that a longitude value is in the valid range [-180, 180].
     */
    public boolean isValidLongitude(double lon) {
        return lon >= -180.0 && lon <= 180.0;
    }

    /**
     * Parses a coordinate string, returning 0.0 if invalid.
     */
    public double parseCoordinate(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
