package model;

import java.time.LocalDateTime;

/**
 * Agreed pickup schedule for an accepted request.
 */
public class PickupSchedule {

    private int           id;
    private int           requestId;
    private LocalDateTime pickupTime;
    private String        notes;
    private LocalDateTime createdAt;

    public PickupSchedule() {}

    // ── Getters ──────────────────────────────────────────────

    public int           getId()         { return id; }
    public int           getRequestId()  { return requestId; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public String        getNotes()      { return notes; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(int id)                          { this.id         = id; }
    public void setRequestId(int requestId)            { this.requestId  = requestId; }
    public void setPickupTime(LocalDateTime pickupTime){ this.pickupTime  = pickupTime; }
    public void setNotes(String notes)                 { this.notes      = notes; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt  = createdAt; }
}
