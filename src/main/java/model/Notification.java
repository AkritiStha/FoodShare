package model;

import java.time.LocalDateTime;

/**
 * In-app notification for a user.
 */
public class Notification {

    private int           id;
    private int           userId;
    private String        message;
    private boolean       read;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int userId, String message) {
        this.userId  = userId;
        this.message = message;
        this.read    = false;
    }

    // ── Getters ──────────────────────────────────────────────

    public int           getId()        { return id; }
    public int           getUserId()    { return userId; }
    public String        getMessage()   { return message; }
    public boolean       isRead()       { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(int id)                          { this.id        = id; }
    public void setUserId(int userId)                  { this.userId    = userId; }
    public void setMessage(String message)             { this.message   = message; }
    public void setRead(boolean read)                  { this.read      = read; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
}