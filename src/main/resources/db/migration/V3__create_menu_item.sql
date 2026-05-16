-- =============================================================
-- V3 - Create menu_item table
--
-- Maps to: MenuItem.java
-- FK: daily_menu_id → daily_menu(id)
-- Referenced by: menu_item_preference.menu_item_id (FK)
-- =============================================================

CREATE TABLE menu_item
(
    id            UUID        NOT NULL DEFAULT gen_random_uuid(),
    daily_menu_id UUID        NOT NULL,
    name          VARCHAR     NOT NULL,
    description   VARCHAR,
    category      VARCHAR     NOT NULL,
    allergens     TEXT[]      NOT NULL DEFAULT '{}',
    dietary_tags  TEXT[]      NOT NULL DEFAULT '{}',
    available     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,

    CONSTRAINT pk_menu_item PRIMARY KEY (id),
    CONSTRAINT fk_menu_item_daily_menu FOREIGN KEY (daily_menu_id) REFERENCES daily_menu (id) ON DELETE CASCADE,
    CONSTRAINT chk_menu_item_category CHECK (
        category IN ('PRIMO', 'SECONDO', 'CONTORNO', 'DESSERT', 'BEVANDA')
        )
);

CREATE INDEX idx_menu_item_daily_menu_id ON menu_item (daily_menu_id);
CREATE INDEX idx_menu_item_category ON menu_item (category);

COMMENT ON TABLE menu_item IS 'Individual dishes within a daily menu.';
COMMENT ON COLUMN menu_item.allergens IS 'EU Regulation 1169/2011 allergens present in this dish. Stored as PostgreSQL text array.';
COMMENT ON COLUMN menu_item.dietary_tags IS 'Dietary tags (VEGETARIAN, VEGAN, GLUTEN_FREE). Stored as PostgreSQL text array.';