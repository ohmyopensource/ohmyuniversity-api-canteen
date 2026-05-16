package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

/**
 * Tracks which campus each student belongs to.
 *
 * This table is populated by the Kafka consumer for the
 * {@code student.campus.assigned} event published by the core service.
 * It allows the canteen service to target menu notifications only to
 * students of the correct campus, without querying the core service directly.
 *
 * If a student changes campus, the existing row is updated in place —
 * a student can only belong to one campus at a time.
 */
@Entity
@Table(
    name = "student_campus_assignment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_student_campus_assignment",
            columnNames = "student_id"
        )
    }
)
public class StudentCampusAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

  /**
   * Opaque reference to the student in the core service.
   */
  @NotBlank
  @Column(name = "student_id", nullable = false, unique = true)
  private String studentId;

  /**
   * Opaque reference to the campus in the core service.
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

  @Column(name = "assigned_at", nullable = false, updatable = false)
  private Instant assignedAt;

  @PrePersist
  void onCreate() {
    assignedAt = Instant.now();
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

  public Instant getAssignedAt() {
    return assignedAt;
  }
}