-- =============================================================
-- V1 - Create canteen table
--
-- Maps to: Canteen.java
-- Referenced by: daily_menu.canteen_id (FK)
-- =============================================================

CREATE TABLE canteen
(
    id                         UUID        NOT NULL DEFAULT gen_random_uuid(),
    campus_id                  VARCHAR     NOT NULL,
    university_id              VARCHAR     NOT NULL,
    name                       VARCHAR     NOT NULL,
    address                    VARCHAR,
    lunch_preference_deadline  TIME,
    dinner_preference_deadline TIME,
    active                     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at                 TIMESTAMP   NOT NULL,
    updated_at                 TIMESTAMP   NOT NULL,

    CONSTRAINT pk_canteen PRIMARY KEY (id),
    CONSTRAINT uq_canteen_campus_name UNIQUE (campus_id, name)
);

CREATE INDEX idx_canteen_campus_id ON canteen (campus_id);
CREATE INDEX idx_canteen_university_id ON canteen (university_id);

COMMENT ON TABLE canteen IS 'Physical canteen locations within university campuses.';
COMMENT ON COLUMN canteen.campus_id IS 'Opaque reference to the campus in ohmyuniversity-core.';
COMMENT ON COLUMN canteen.university_id IS 'Opaque reference to the university in ohmyuniversity-core.';
COMMENT ON COLUMN canteen.lunch_preference_deadline IS 'Time by which students must submit lunch preferences. Null means no deadline.';
COMMENT ON COLUMN canteen.dinner_preference_deadline IS 'Time by which students must submit dinner preferences. Null means no deadline.';