package util;

import java.util.Comparator;
import java.util.List;

import model.FoodItem;

/**
 * Utility class that implements the Haversine formula for calculating
 * geographic distances between two coordinate pairs, and provides a
 * convenience method for sorting food listings by proximity.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the utility ({@code util}) layer and is invoked
 * from {@link service.LocationService} and indirectly from
 * {@link service.FoodService#searchAvailable(String, double, double)}.
 * It enables the location-based food matching feature: when an NGO
 * searches for available food, results are re-ordered so that the
 * nearest food item appears first, improving the likelihood of a
 * successful, low-cost pickup.
 *
 * <p><strong>Algorithm:</strong><br>
 * The <em>Haversine formula</em> computes the shortest distance (great-circle
 * distance) between two points on the surface of a sphere, given their
 * latitudes and longitudes in decimal degrees. It accounts for the curvature
 * of the Earth using the mean Earth radius of {@value #EARTH_RADIUS_KM} km.
 * The formula is well-suited to distances of up to a few hundred kilometres
 * where the flat-Earth approximation would introduce unacceptable error.
 *
 * <p><strong>Reference:</strong>
 * <a href="https://en.wikipedia.org/wiki/Haversine_formula">
 * Haversine formula – Wikipedia</a>
 *
 * <p>This class is not instantiable; all members are static.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.LocationService
 * @see     service.FoodService
 */

public class DistanceCalculator {

    /**
     * Mean radius of the Earth in kilometres, used as the sphere radius in
     * the Haversine formula.
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Private constructor — prevents instantiation of this utility class.
     */

    private DistanceCalculator() {}

    /**
     * Calculates the great-circle distance in kilometres between two
     * geographic coordinates using the Haversine formula.
     *
     * <p><strong>Formula steps:</strong>
     * <ol>
     *   <li>Convert the latitude and longitude differences from degrees to
     *       radians.</li>
     *   <li>Apply the Haversine formula:
     *       {@code a = sin²(Δlat/2) + cos(lat1)·cos(lat2)·sin²(Δlon/2)}</li>
     *   <li>Compute the central angle:
     *       {@code c = 2·atan2(√a, √(1−a))}</li>
     *   <li>Multiply by the Earth's radius to obtain the distance.</li>
     * </ol>
     *
     * <p>The result is rounded to two decimal places before being returned.
     *
     * @param lat1 the latitude of the first point, in decimal degrees
     *             (valid range: −90.0 to 90.0)
     * @param lon1 the longitude of the first point, in decimal degrees
     *             (valid range: −180.0 to 180.0)
     * @param lat2 the latitude of the second point, in decimal degrees
     * @param lon2 the longitude of the second point, in decimal degrees
     * @return the great-circle distance between the two points, in kilometres,
     *         rounded to two decimal places
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
     * Annotates each {@link FoodItem} in the supplied list with its
     * calculated distance from a given origin coordinate, then sorts the
     * list in ascending order of distance (nearest first).
     *
     * <p><strong>Behaviour:</strong>
     * <ol>
     *   <li>Iterates over every {@link FoodItem} in {@code items}.</li>
     *   <li>Calls {@link #calculate(double, double, double, double)} with the
     *       origin coordinates and each item's stored latitude/longitude.</li>
     *   <li>Writes the computed value to the item's transient
     *       {@code distanceKm} field via
     *       {@link FoodItem#setDistanceKm(double)}.</li>
     *   <li>Sorts the list in-place using
     *       {@link FoodItem#getDistanceKm()} as the comparator key.</li>
     * </ol>
     *
     * <p>The {@code distanceKm} field on {@link FoodItem} is transient (not
     * persisted to the database) and is populated solely by this method for
     * display purposes in the NGO search results view.
     *
     * <p>If either {@code originLat} and {@code originLon} are both {@code 0.0}
     * (indicating that the NGO has not provided their location), calling code
     * in {@link service.FoodService} skips this method, leaving the list in
     * its default database-returned order.
     *
     * @param items     the mutable list of {@link FoodItem} objects to annotate
     *                  and sort; must not be {@code null}
     * @param originLat the latitude of the NGO's current location, in decimal
     *                  degrees; used as the distance origin
     * @param originLon the longitude of the NGO's current location, in decimal
     *                  degrees; used as the distance origin
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
