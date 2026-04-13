package util;

import java.util.Comparator;
import java.util.List;

import model.FoodItem;

/**
 * Haversine-based distance calculator.
 *
 * The Haversine formula computes the great-circle distance between two points
 * on a sphere given their latitude/longitude in degrees.
 *
 * Reference: https://en.wikipedia.org/wiki/Haversine_formula
 */
public class DistanceCalculator {

    /** Earth's mean radius in kilometres. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator() {}

    /**
     * Calculates the distance in kilometres between two geographic coordinates.
     *
     * @param lat1 latitude  of point 1 (degrees)
     * @param lon1 longitude of point 1 (degrees)
     * @param lat2 latitude  of point 2 (degrees)
     * @param lon2 longitude of point 2 (degrees)
     * @return distance in kilometres (rounded to 2 decimal places)
     */
    public static double calculate(double lat1, double lon1,
                                   double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;

        // Round to 2 decimal places
        return Math.round(distanceKm * 100.0) / 100.0;
    }

    /**
     * Annotates each FoodItem in the list with its distance from the given
     * origin coordinates, then sorts the list nearest-first.
     *
     * @param items      list of food items to sort
     * @param originLat  observer's latitude
     * @param originLon  observer's longitude
     */
    public static void sortByDistance(List<FoodItem> items,
                                      double originLat,
                                      double originLon) {
        for (FoodItem item : items) {
            double dist = calculate(originLat, originLon,
                    item.getLatitude(), item.getLongitude());
            item.setDistanceKm(dist);
        }
        items.sort(Comparator.comparingDouble(FoodItem::getDistanceKm));
    }
}
