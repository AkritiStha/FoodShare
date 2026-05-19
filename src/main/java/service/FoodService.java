package service;

import dao.FoodItemDAO;
import model.FoodItem;
import util.DistanceCalculator;
import util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for food item operations.
 */
public class FoodService {

    private final FoodItemDAO foodItemDAO = new FoodItemDAO();

    /**
     * Adds a new food listing after validating all inputs.
     *
     * @return null on success or an error message string
     */
    public String addFoodItem(int donorId, String name, String quantityStr,
                              String quantityUnit, String description,
                              String expiryStr, String pickupLocation,
                              String latStr, String lonStr) {
        String err;
        if ((err = ValidationUtil.validateName(name)) != null) return err;
        if ((err = ValidationUtil.validateQuantity(quantityStr)) != null) return err;
        if ((err = ValidationUtil.validateExpiryDate(expiryStr)) != null) return err;
        if ((err = ValidationUtil.validateAddress(pickupLocation)) != null) return err;

        double lat = parseCoord(latStr);
        double lon = parseCoord(lonStr);

        try {
            FoodItem item = new FoodItem();
            item.setDonorId(donorId);
            item.setName(name.trim());
            item.setQuantity(new BigDecimal(quantityStr.trim()));
            item.setQuantityUnit(quantityUnit == null ? "kg" : quantityUnit.trim());
            item.setDescription(description == null ? "" : description.trim());
            item.setExpiryDate(ValidationUtil.parseDateTime(expiryStr));
            item.setPickupLocation(pickupLocation.trim());
            item.setLatitude(lat);
            item.setLongitude(lon);

            int id = foodItemDAO.createFoodItem(item);
            return id > 0 ? null : "Failed to add food item.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    public String updateFoodItem(int foodItemId, int donorId, String name,
                                 String quantityStr, String quantityUnit,
                                 String description, String expiryStr,
                                 String pickupLocation, String latStr, String lonStr) {
        String err;
        if ((err = ValidationUtil.validateName(name)) != null) return err;
        if ((err = ValidationUtil.validateQuantity(quantityStr)) != null) return err;
        if ((err = ValidationUtil.validateExpiryDate(expiryStr)) != null) return err;
        if ((err = ValidationUtil.validateAddress(pickupLocation)) != null) return err;

        try {
            FoodItem item = new FoodItem();
            item.setId(foodItemId);
            item.setDonorId(donorId);
            item.setName(name.trim());
            item.setQuantity(new BigDecimal(quantityStr.trim()));
            item.setQuantityUnit(quantityUnit == null ? "kg" : quantityUnit.trim());
            item.setDescription(description == null ? "" : description.trim());
            item.setExpiryDate(ValidationUtil.parseDateTime(expiryStr));
            item.setPickupLocation(pickupLocation.trim());
            item.setLatitude(parseCoord(latStr));
            item.setLongitude(parseCoord(lonStr));
            foodItemDAO.updateFoodItem(item);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    public String deleteFoodItem(int id, int donorId) {
        try {
            foodItemDAO.deleteFoodItem(id, donorId);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete food item.";
        }
    }

    public FoodItem getById(int id) throws SQLException {
        return foodItemDAO.findById(id);
    }

    public List<FoodItem> getDonorListings(int donorId) throws SQLException {
        return foodItemDAO.findByDonor(donorId);
    }

    /**
     * Returns available food sorted by distance from the NGO's location.
     * If keyword is provided, filters by food name/description.
     */
    public List<FoodItem> searchAvailable(String keyword, double ngoLat, double ngoLon)
            throws SQLException {
        List<FoodItem> items = ValidationUtil.isBlank(keyword)
                ? foodItemDAO.findAvailable()
                : foodItemDAO.searchAvailable(keyword.trim());

        // Sort nearest-first using Haversine formula
        if (ngoLat != 0 || ngoLon != 0) {
            DistanceCalculator.sortByDistance(items, ngoLat, ngoLon);
        }
        return items;
    }

    public List<FoodItem> getAllFoodItems() throws SQLException {
        return foodItemDAO.findAll();
    }

    public void adminDeleteFood(int id) throws SQLException {
        foodItemDAO.adminDeleteFoodItem(id);
    }

    public void markExpired() throws SQLException {
        foodItemDAO.markExpiredItems();
    }

    public int countAll() throws SQLException {
        return foodItemDAO.countAll();
    }

    public double totalFoodSaved() throws SQLException {
        return foodItemDAO.totalFoodSaved();
    }

    private double parseCoord(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
