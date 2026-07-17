package org.ohmyopensource.ohmyuniversity.canteen.kafka.consumer;

import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.StudentCampusAssignment;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.StudentCampusAssignmentRepository;
import org.ohmyopensource.ohmyuniversity.canteen.kafka.event.CampusAssignmentDiscoveredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for the {@code campus-assignment.discovered} topic.
 *
 * <p>The core service publishes this event when a sync against Cineca reveals that a
 * student is associated with a campus. This consumer upserts the StudentCampusAssignment
 * record so the canteen service knows which campus each student belongs to, enabling
 * correct menu targeting.
 */
@Component
public class CampusAssignmentDiscoveredConsumer {

  private static final Logger log =
      LoggerFactory.getLogger(CampusAssignmentDiscoveredConsumer.class);

  private final StudentCampusAssignmentRepository studentCampusAssignmentRepository;

  public CampusAssignmentDiscoveredConsumer(
      StudentCampusAssignmentRepository studentCampusAssignmentRepository) {
    this.studentCampusAssignmentRepository = studentCampusAssignmentRepository;
  }

  @KafkaListener(
      topics = "campus-assignment.discovered",
      groupId = "ohmyuniversity-canteen",
      containerFactory = "campusAssignmentDiscoveredContainerFactory"
  )
  @Transactional
  public void consume(CampusAssignmentDiscoveredEvent event) {
    log.debug("Received campus-assignment.discovered event: studentId={} campusId={}",
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