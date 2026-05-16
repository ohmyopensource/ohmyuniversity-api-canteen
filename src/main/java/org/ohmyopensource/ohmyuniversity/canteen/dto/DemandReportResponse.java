package org.ohmyopensource.ohmyuniversity.canteen.dto;

import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for the demand report endpoint.
 * Used by GET /api/canteens/{canteenId}/menus/{menuId}/demand-report.
 *
 * The counts map contains menuItemId -> preference count pairs.
 * Only available when the menu is CLOSED.
 */
public class DemandReportResponse {

  private UUID menuId;

  /**
   * Map of menuItemId to preference count.
   * E.g. {"uuid-pasta": 234, "uuid-insalata": 89}
   */
  private Map<UUID, Long> counts;

  public UUID getMenuId() {
    return menuId;
  }

  public void setMenuId(UUID menuId) {
    this.menuId = menuId;
  }

  public Map<UUID, Long> getCounts() {
    return counts;
  }

  public void setCounts(Map<UUID, Long> counts) {
    this.counts = counts;
  }
}