package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Represents a physical canteen location within a university campus.
 *
 * A campus can have multiple canteens (e.g. "Mensa Centrale", "Mensa Medicina").
 * Each canteen publishes its own daily menus independently.
 *
 * The campusId and universityId are opaque references to the core service —
 * the canteen service never queries the core directly. These values arrive
 * via Kafka events and are stored as plain strings.
 */
@Entity
@Table(
    name = "canteen",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_canteen_campus_name",
            columnNames = {"campus_id", "name"}
        )
    }
)
public class Canteen {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

  /**
   * Opaque reference to the campus in the core service.
   * Used for targeting — menus are visible only to students of this campus.
   */
  @NotBlank
  @Column(name = "campus_id", nullable = false)
  private String campusId;

  /**
   * Opaque reference to the university in the core service.
   */
  @NotBlank
  @Column(name = "university_id", nullable = false)
  private String universityId;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "address")
  private String address;

  /**
   * Time by which students must submit their preferences for lunch.
   * Null means no deadline — preferences accepted until menu is manually closed.
   */
  @Column(name = "lunch_preference_deadline")
  private LocalTime lunchPreferenceDeadline;

  /**
   * Time by which students must submit their preferences for dinner.
   */
  @Column(name = "dinner_preference_deadline")
  private LocalTime dinnerPreferenceDeadline;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getCampusId() {
    return campusId;
  }

  public void setCampusId(String campusId) {
    this.campusId = campusId;
  }

  public String getUniversityId() {
    return universityId;
  }

  public void setUniversityId(String universityId) {
    this.universityId = universityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalTime getLunchPreferenceDeadline() {
    return lunchPreferenceDeadline;
  }

  public void setLunchPreferenceDeadline(LocalTime lunchPreferenceDeadline) {
    this.lunchPreferenceDeadline = lunchPreferenceDeadline;
  }

  public LocalTime getDinnerPreferenceDeadline() {
    return dinnerPreferenceDeadline;
  }

  public void setDinnerPreferenceDeadline(LocalTime dinnerPreferenceDeadline) {
    this.dinnerPreferenceDeadline = dinnerPreferenceDeadline;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}