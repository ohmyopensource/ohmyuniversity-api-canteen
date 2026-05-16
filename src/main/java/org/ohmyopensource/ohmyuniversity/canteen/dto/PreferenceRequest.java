package org.ohmyopensource.ohmyuniversity.canteen.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for submitting a meal preference.
 * Used by POST /api/canteens/{canteenId}/menus/{menuId}/preferences.
 */
public class PreferenceRequest {

  @NotNull
  private UUID menuItemId;

  public UUID getMenuItemId() {
    return menuItemId;
  }

  public void setMenuItemId(UUID menuItemId) {
    this.menuItemId = menuItemId;
  }
}