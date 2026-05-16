package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a student's preference for a specific menu item.
 *
 * One row per (student, menu item) pair — a student can express
 * preference for multiple items in the same menu (e.g. a primo and a secondo).
 * The unique constraint prevents duplicate preferences for the same item.
 *
 * Preferences are immutable once submitted — no edit or delete in Sprint 1.
 * The canteen manager reads aggregated counts, not individual responses.
 */
@Entity
@Table(
    name = "menu_item_preference",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_preference_student_item",
            columnNames = {"student_id", "menu_item_id"}
        )
    }
)
public class MenuItemPreference {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

  /**
   * Opaque reference to the student in the core service.
   * Comes from the X-User-Id header forwarded by the gateway.
   */
  @NotBlank
  @Column(name = "student_id", nullable = false)
  private String studentId;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_item_id", nullable = false)
  private MenuItem menuItem;

  @Column(name = "submitted_at", nullable = false, updatable = false)
  private Instant submittedAt;

  @PrePersist
  void onCreate() {
    submittedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public Instant getSubmittedAt() {
    return submittedAt;
  }
}