-- =============================================================
-- V2 - Create daily_menu table
--
-- Maps to: DailyMenu.java
-- FK: canteen_id → canteen(id)
-- Referenced by: menu_item.daily_menu_id (FK)
-- =============================================================

CREATE TABLE daily_menu
(
    id           UUID        NOT NULL DEFAULT gen_random_uuid(),
    canteen_id   UUID        NOT NULL,
    date         DATE        NOT NULL,
    meal_type    VARCHAR     NOT NULL,
    status       VARCHAR     NOT NULL DEFAULT 'DRAFT',
    notes        VARCHAR,
    published_at TIMESTAMP,
    closed_at    TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,

    CONSTRAINT pk_daily_menu PRIMARY KEY (id),
    CONSTRAINT fk_daily_menu_canteen FOREIGN KEY (canteen_id) REFERENCES canteen (id) ON DELETE CASCADE,
    CONSTRAINT uq_daily_menu_canteen_date_meal UNIQUE (canteen_id, date, meal_type),
    CONSTRAINT chk_daily_menu_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'CLOSED')),
    CONSTRAINT chk_daily_menu_meal_type CHECK (meal_type IN ('LUNCH', 'DINNER'))
);

CREATE INDEX idx_daily_menu_canteen_id ON daily_menu (canteen_id);
CREATE INDEX idx_daily_menu_date ON daily_menu (date);
CREATE INDEX idx_daily_menu_status ON daily_menu (status);

COMMENT ON TABLE daily_menu IS 'Daily menus published by canteen managers.';
COMMENT ON COLUMN daily_menu.status IS 'Lifecycle state: DRAFT | PUBLISHED | CLOSED.';
COMMENT ON COLUMN daily_menu.published_at IS 'When the menu became visible to students.';
COMMENT ON COLUMN daily_menu.closed_at IS 'When the preference window was closed.';