package model;

import java.time.LocalDateTime;

/**
 * Represents an NGO's request for a food item.
 */
public class Request {

    private int           id;
    private int           foodItemId;
    private int           ngoId;
    private int           donorId;
    private String        status;          // PENDING | ACCEPTED | COMPLETED | REJECTED | EXPIRED
    private String        message;
    private Integer       rating;          // 1–5 nullable
    private String        ratingNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields (read-only from query)
    private String        foodItemName;
    private String        ngoName;
    private String        donorName;
    private String        pickupLocation;
    private LocalDateTime pickupTime;      // from pickup_schedules join

    public Request() {}

    // ── Getters ──────────────────────────────────────────────

    public int           getId()             { return id; }
    public int           getFoodItemId()     { return foodItemId; }
    public int           getNgoId()          { return ngoId; }
    public int           getDonorId()        { return donorId; }
    public String        getStatus()         { return status; }
    public String        getMessage()        { return message; }
    public Integer       getRating()         { return rating; }
    public String        getRatingNote()     { return ratingNote; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public String        getFoodItemName()   { return foodItemName; }
    public String        getNgoName()        { return ngoName; }
    public String        getDonorName()      { return donorName; }
    public String        getPickupLocation() { return pickupLocation; }
    public LocalDateTime getPickupTime()     { return pickupTime; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(int id)                             { this.id             = id; }
    public void setFoodItemId(int foodItemId)             { this.foodItemId     = foodItemId; }
    public void setNgoId(int ngoId)                       { this.ngoId          = ngoId; }
    public void setDonorId(int donorId)                   { this.donorId        = donorId; }
    public void setStatus(String status)                  { this.status         = status; }
    public void setMessage(String message)                { this.message        = message; }
    public void setRating(Integer rating)                 { this.rating         = rating; }
    public void setRatingNote(String ratingNote)          { this.ratingNote     = ratingNote; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt      = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt      = updatedAt; }
    public void setFoodItemName(String foodItemName)      { this.foodItemName   = foodItemName; }
    public void setNgoName(String ngoName)                { this.ngoName        = ngoName; }
    public void setDonorName(String donorName)            { this.donorName      = donorName; }
    public void setPickupLocation(String pickupLocation)  { this.pickupLocation = pickupLocation; }
    public void setPickupTime(LocalDateTime pickupTime)   { this.pickupTime     = pickupTime; }
}
