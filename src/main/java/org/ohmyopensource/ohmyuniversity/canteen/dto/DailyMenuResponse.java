package org.ohmyopensource.ohmyuniversity.canteen.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MealType;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuStatus;

/**
 * Response DTO for daily menu endpoints.
 */
public class DailyMenuResponse {

  private UUID id;
  private UUID canteenId;
  private LocalDate date;
  private MealType mealType;
  private MenuStatus status;
  private String notes;
  private Instant publishedAt;
  private Instant closedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getCanteenId() {
    return canteenId;
  }

  public void setCanteenId(UUID canteenId) {
    this.canteenId = canteenId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public MealType getMealType() {
    return mealType;
  }

  public void setMealType(MealType mealType) {
    this.mealType = mealType;
  }

  public MenuStatus getStatus() {
    return status;
  }

  public void setStatus(MenuStatus status) {
    this.status = status;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Instant publishedAt) {
    this.publishedAt = publishedAt;
  }

  public Instant getClosedAt() {
    return closedAt;
  }

  public void setClosedAt(Instant closedAt) {
    this.closedAt = closedAt;
  }
}