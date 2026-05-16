package org.ohmyopensource.ohmyuniversity.canteen.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.StudentCampusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link StudentCampusAssignment} entities.
 */
@Repository
public interface StudentCampusAssignmentRepository
    extends JpaRepository<StudentCampusAssignment, UUID> {

  /**
   * Find the campus assignment for a student.
   * Used to validate that a student belongs to the correct campus
   * before allowing preference submission.
   *
   * @param studentId the opaque student ID from the gateway header
   * @return the assignment if it exists
   */
  Optional<StudentCampusAssignment> findByStudentId(String studentId);

  /**
   * Check whether a student has a campus assignment.
   *
   * @param studentId the opaque student ID
   * @return true if the student has been assigned to a campus
   */
  boolean existsByStudentId(String studentId);
}