package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the daily menu for a specific canteen, date and meal type.
 *
 * A canteen can have at most one menu per date per meal type.
 * The menu starts as DRAFT, is published by the canteen manager,
 * and is closed when the preference window ends.
 */
@Entity
@Table(
    name = "daily_menu",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_daily_menu_canteen_date_meal",
            columnNames = {"canteen_id", "date", "meal_type"}
        )
    }
)
public class DailyMenu {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "canteen_id", nullable = false)
  private Canteen canteen;

  @NotNull
  @Column(name = "date", nullable = false)
  private LocalDate date;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "meal_type", nullable = false)
  private MealType mealType;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private MenuStatus status;

  /**
   * Optional notes from the canteen manager visible to students.
   * E.g. "Oggi chiusura anticipata alle 14:00".
   */
  @Column(name = "notes")
  private String notes;

  /**
   * When the menu was published and became visible to students.
   * Null if still in DRAFT.
   */
  @Column(name = "published_at")
  private Instant publishedAt;

  /**
   * When the preference window was closed.
   * Null if not yet closed.
   */
  @Column(name = "closed_at")
  private Instant closedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
    if (status == null) {
      status = MenuStatus.DRAFT;
    }
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public Canteen getCanteen() {
    return canteen;
  }

  public void setCanteen(Canteen canteen) {
    this.canteen = canteen;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}