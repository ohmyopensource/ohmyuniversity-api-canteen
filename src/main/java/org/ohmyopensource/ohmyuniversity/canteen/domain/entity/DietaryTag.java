package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

/**
 * Dietary tags for a menu item.
 * Used to filter menu items based on dietary preferences.
 */
public enum DietaryTag {

  /** Contains no meat, poultry or fish. */
  VEGETARIAN,

  /** Contains no animal products. */
  VEGAN,

  /** Contains no gluten. */
  GLUTEN_FREE
}