package org.ohmyopensource.ohmyuniversity.canteen.kafka.consumer;

import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.StudentCampusAssignment;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.StudentCampusAssignmentRepository;
import org.ohmyopensource.ohmyuniversity.canteen.kafka.event.StudentCampusAssignedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for the {@code student.campus.assigned} topic.
 *
 * The core service publishes this event when a student is assigned
 * to a campus. This consumer upserts the StudentCampusAssignment record
 * so the canteen service knows which campus each student belongs to,
 * enabling correct menu targeting.
 */
@Component
public class StudentCampusAssignedConsumer {

  private static final Logger log =
      LoggerFactory.getLogger(StudentCampusAssignedConsumer.class);

  private final StudentCampusAssignmentRepository studentCampusAssignmentRepository;

  public StudentCampusAssignedConsumer(
      StudentCampusAssignmentRepository studentCampusAssignmentRepository) {
    this.studentCampusAssignmentRepository = studentCampusAssignmentRepository;
  }

  @KafkaListener(
      topics = "student.campus.assigned",
      groupId = "ohmyuniversity-canteen",
      containerFactory = "studentCampusAssignedContainerFactory"
  )
  @Transactional
  public void consume(StudentCampusAssignedEvent event) {
    log.debug("Received student.campus.assigned event: studentId={} campusId={}",
        event.studentId(), event.campusId());

    studentCampusAssignmentRepository
        .findByStudentId(event.studentId())
        .ifPresentOrElse(
            existing -> {
              existing.setCampusId(event.campusId());
              existing.setUniversityId(event.universityId());
              studentCampusAssignmentRepository.save(existing);
              log.info("Campus assignment updated: studentId={} campusId={}",
                  event.studentId(), event.campusId());
            },
            () -> {
              StudentCampusAssignment assignment = new StudentCampusAssignment();
              assignment.setStudentId(event.studentId());
              assignment.setCampusId(event.campusId());
              assignment.setUniversityId(event.universityId());
              studentCampusAssignmentRepository.save(assignment);
              log.info("Campus assignment created: studentId={} campusId={}",
                  event.studentId(), event.campusId());
            }
        );
  }
}