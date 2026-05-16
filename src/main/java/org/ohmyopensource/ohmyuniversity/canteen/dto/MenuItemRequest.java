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

  private AllergenType[] allergens = new AllergenType[0];

  private DietaryTag[] dietaryTags = new DietaryTag[0];

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

  public AllergenType[] getAllergens() {
    return allergens;
  }

  public void setAllergens(AllergenType[] allergens) {
    this.allergens = allergens;
  }

  public DietaryTag[] getDietaryTags() {
    return dietaryTags;
  }

  public void setDietaryTags(DietaryTag[] dietaryTags) {
    this.dietaryTags = dietaryTags;
  }
}