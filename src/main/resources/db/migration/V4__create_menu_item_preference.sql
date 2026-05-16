-- =============================================================
-- V4 - Create menu_item_preference table
--
-- Maps to: MenuItemPreference.java
-- FK: menu_item_id → menu_item(id)
-- =============================================================

CREATE TABLE menu_item_preference
(
    id           UUID        NOT NULL DEFAULT gen_random_uuid(),
    student_id   VARCHAR     NOT NULL,
    menu_item_id UUID        NOT NULL,
    submitted_at TIMESTAMP   NOT NULL,

    CONSTRAINT pk_menu_item_preference PRIMARY KEY (id),
    CONSTRAINT fk_preference_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_item (id) ON DELETE CASCADE,
    CONSTRAINT uq_preference_student_item UNIQUE (student_id, menu_item_id)
);

CREATE INDEX idx_preference_menu_item_id ON menu_item_preference (menu_item_id);
CREATE INDEX idx_preference_student_id ON menu_item_preference (student_id);
CREATE INDEX idx_preference_submitted_at ON menu_item_preference (submitted_at);

COMMENT ON TABLE menu_item_preference IS 'Student meal preferences per menu item. One row per (student, item) pair.';
COMMENT ON COLUMN menu_item_preference.student_id IS 'Opaque reference to the student in ohmyuniversity-core.';