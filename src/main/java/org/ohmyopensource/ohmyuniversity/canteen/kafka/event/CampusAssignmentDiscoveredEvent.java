package org.ohmyopensource.ohmyuniversity.canteen.kafka.event;

/**
 * Payload of the {@code campus-assignment.discovered} Kafka event.
 *
 * <p>Published by ohmyuniversity-core when a sync against Cineca reveals that a
 * student is associated with a campus.
 *
 * @param studentId    opaque user ID from the core service
 * @param campusId     opaque campus ID from the core service
 * @param universityId opaque university ID from the core service
 */
public record CampusAssignmentDiscoveredEvent(
    String studentId,
    String campusId,
    String universityId
) {
}