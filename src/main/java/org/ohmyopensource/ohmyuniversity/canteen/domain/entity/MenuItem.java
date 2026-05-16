package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Represents a single item in a daily menu.
 *
 * Allergens and dietary tags are stored as PostgreSQL native arrays
 * using Hibernate 6+ {@link Array} annotation. This avoids separate
 * join tables for what are effectively fixed enum sets.
 */
@Entity
@Table(name = "menu_item")
public class MenuItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "daily_menu_id", nullable = false)
  private DailyMenu dailyMenu;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private MenuItemCategory category;

  /**
   * Allergen names stored as strings (e.g. "GLUTEN", "MILK").
   * Values correspond to AllergenType enum names.
   * Stored as PostgreSQL text[]. Conversion to enum happens in the DTO layer.
   */
  @Array(length = 14)
  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "allergens", columnDefinition = "text[]")
  private String[] allergens = new String[0];

  /**
   * Dietary tag names stored as strings (e.g. "VEGAN", "GLUTEN_FREE").
   * Values correspond to DietaryTag enum names.
   * Stored as PostgreSQL text[]. Conversion to enum happens in the DTO layer.
   */
  @Array(length = 3)
  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "dietary_tags", columnDefinition = "text[]")
  private String[] dietaryTags = new String[0];

  /**
   * Whether this item is available today.
   * Can be toggled by the canteen manager if a dish runs out.
   */
  @Column(name = "available", nullable = false)
  private boolean available = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public DailyMenu getDailyMenu() {
    return dailyMenu;
  }

  public void setDailyMenu(DailyMenu dailyMenu) {
    this.dailyMenu = dailyMenu;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public MenuItemCategory getCategory() {
    return category;
  }

  public void setCategory(MenuItemCategory category) {
    this.category = category;
  }

  public String[] getAllergens() {
    return allergens;
  }

  public void setAllergens(String[] allergens) {
    this.allergens = allergens;
  }

  public String[] getDietaryTags() {
    return dietaryTags;
  }

  public void setDietaryTags(String[] dietaryTags) {
    this.dietaryTags = dietaryTags;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}