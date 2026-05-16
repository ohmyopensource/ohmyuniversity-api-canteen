package org.ohmyopensource.ohmyuniversity.canteen.kafka.event;

/**
 * Payload of the {@code student.campus.assigned} Kafka event.
 * Published by ohmyuniversity-core when a student is assigned to a campus.
 *
 * @param studentId    opaque user ID from the core service
 * @param campusId     opaque campus ID from the core service
 * @param universityId opaque university ID from the core service
 */
public record StudentCampusAssignedEvent(
    String studentId,
    String campusId,
    String universityId
) {
}