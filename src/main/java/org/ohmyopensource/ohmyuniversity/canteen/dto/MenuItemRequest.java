package org.ohmyopensource.ohmyuniversity.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.AllergenType;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DietaryTag;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItemCategory;

/**
 * Request DTO for creating or updating a menu item.
 * Used by POST /api/canteens/{canteenId}/menus/{menuId}/items
 * and PUT /api/canteens/{canteenId}/menus/{menuId}/items/{itemId}.
 */
public class MenuItemRequest {

  @NotBlank
  private String name;

  private String description;

  @NotNull
  private MenuItemCategory category;

  /**
   * Allergen names — must match AllergenType enum values (e.g. "GLUTEN", "MILK").
   */
  private String[] allergens = new String[0];

  /**
   * Dietary tag names — must match DietaryTag enum values (e.g. "VEGAN").
   */
  private String[] dietaryTags = new String[0];

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
}