package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a food listing created by a donor.
 */
public class FoodItem {

    private int           id;
    private int           donorId;
    private String        donorName;       // joined from users table (read-only)
    private String        name;
    private BigDecimal    quantity;
    private String        quantityUnit;
    private String        description;
    private LocalDateTime expiryDate;
    private String        pickupLocation;
    private double        latitude;
    private double        longitude;
    private String        status;          // available | requested | completed | expired
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient field – populated by location query
    private double distanceKm;

    public FoodItem() {}

    // ── Getters ──────────────────────────────────────────────

    public int           getId()             { return id; }
    public int           getDonorId()        { return donorId; }
    public String        getDonorName()      { return donorName; }
    public String        getName()           { return name; }
    public BigDecimal    getQuantity()       { return quantity; }
    public String        getQuantityUnit()   { return quantityUnit; }
    public String        getDescription()    { return description; }
    public LocalDateTime getExpiryDate()     { return expiryDate; }
    public String        getPickupLocation() { return pickupLocation; }
    public double        getLatitude()       { return latitude; }
    public double        getLongitude()      { return longitude; }
    public String        getStatus()         { return status; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public double        getDistanceKm()     { return distanceKm; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(int id)                             { this.id             = id; }
    public void setDonorId(int donorId)                   { this.donorId        = donorId; }
    public void setDonorName(String donorName)            { this.donorName      = donorName; }
    public void setName(String name)                      { this.name           = name; }
    public void setQuantity(BigDecimal quantity)          { this.quantity       = quantity; }
    public void setQuantityUnit(String quantityUnit)      { this.quantityUnit   = quantityUnit; }
    public void setDescription(String description)        { this.description    = description; }
    public void setExpiryDate(LocalDateTime expiryDate)   { this.expiryDate     = expiryDate; }
    public void setPickupLocation(String pickupLocation)  { this.pickupLocation = pickupLocation; }
    public void setLatitude(double latitude)              { this.latitude       = latitude; }
    public void setLongitude(double longitude)            { this.longitude      = longitude; }
    public void setStatus(String status)                  { this.status         = status; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt      = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt      = updatedAt; }
    public void setDistanceKm(double distanceKm)          { this.distanceKm     = distanceKm; }

    // ── Business helpers ─────────────────────────────────────

    /** Returns true if the food expires within the next 24 hours. */
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), expiryDate);
        return hoursLeft >= 0 && hoursLeft <= 24;
    }

    /** Returns true if expiry date has already passed. */
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
