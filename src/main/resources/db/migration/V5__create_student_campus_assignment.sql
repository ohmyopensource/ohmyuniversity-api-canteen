-- =============================================================
-- V5 - Create student_campus_assignment table
--
-- Maps to: StudentCampusAssignment.java
-- Populated by Kafka consumer for student.campus.assigned events.
-- Used for targeting — menus are visible only to students of the
-- correct campus.
-- =============================================================

CREATE TABLE student_campus_assignment
(
    id            UUID        NOT NULL DEFAULT gen_random_uuid(),
    student_id    VARCHAR     NOT NULL,
    campus_id     VARCHAR     NOT NULL,
    university_id VARCHAR     NOT NULL,
    assigned_at   TIMESTAMP   NOT NULL,

    CONSTRAINT pk_student_campus_assignment PRIMARY KEY (id),
    CONSTRAINT uq_student_campus_assignment UNIQUE (student_id)
);

CREATE INDEX idx_student_campus_assignment_campus_id ON student_campus_assignment (campus_id);
CREATE INDEX idx_student_campus_assignment_university_id ON student_campus_assignment (university_id);

COMMENT ON TABLE student_campus_assignment IS 'Tracks which campus each student belongs to. Populated via Kafka from ohmyuniversity-core.';
COMMENT ON COLUMN student_campus_assignment.student_id IS 'Opaque reference to the student in ohmyuniversity-core. One row per student.';
COMMENT ON COLUMN student_campus_assignment.campus_id IS 'Opaque reference to the campus in ohmyuniversity-core.';