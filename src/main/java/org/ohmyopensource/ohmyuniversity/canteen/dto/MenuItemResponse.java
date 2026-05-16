package org.ohmyopensource.ohmyuniversity.canteen.dto;

import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.AllergenType;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DietaryTag;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItemCategory;

/**
 * Response DTO for menu item endpoints.
 */
public class MenuItemResponse {

  private UUID id;
  private String name;
  private String description;
  private MenuItemCategory category;
  private AllergenType[] allergens;
  private DietaryTag[] dietaryTags;
  private boolean available;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}