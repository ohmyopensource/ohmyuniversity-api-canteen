package org.ohmyopensource.ohmyuniversity.canteen.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.Canteen;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DailyMenu;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItem;
import org.ohmyopensource.ohmyuniversity.canteen.dto.DailyMenuRequest;
import org.ohmyopensource.ohmyuniversity.canteen.dto.DailyMenuResponse;
import org.ohmyopensource.ohmyuniversity.canteen.dto.DemandReportResponse;
import org.ohmyopensource.ohmyuniversity.canteen.dto.MenuItemRequest;
import org.ohmyopensource.ohmyuniversity.canteen.dto.MenuItemResponse;
import org.ohmyopensource.ohmyuniversity.canteen.dto.PreferenceRequest;
import org.ohmyopensource.ohmyuniversity.canteen.service.CanteenService;
import org.ohmyopensource.ohmyuniversity.canteen.service.DailyMenuService;
import org.ohmyopensource.ohmyuniversity.canteen.service.MenuItemService;
import org.ohmyopensource.ohmyuniversity.canteen.service.MenuPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for daily menu management, menu items and preferences.
 *
 * All endpoints are nested under /api/canteens/{canteenId}/menus.
 * The userId is read from the X-User-Id header forwarded by the gateway.
 */
@RestController
@RequestMapping("/api/canteens/{canteenId}/menus")
public class DailyMenuController {

  private static final String USER_ID_HEADER = "X-User-Id";

  private final CanteenService canteenService;
  private final DailyMenuService dailyMenuService;
  private final MenuItemService menuItemService;
  private final MenuPreferenceService menuPreferenceService;

  public DailyMenuController(
      CanteenService canteenService,
      DailyMenuService dailyMenuService,
      MenuItemService menuItemService,
      MenuPreferenceService menuPreferenceService) {
    this.canteenService = canteenService;
    this.dailyMenuService = dailyMenuService;
    this.menuItemService = menuItemService;
    this.menuPreferenceService = menuPreferenceService;
  }

  // ================================
  // Menu endpoints
  // ================================

  /**
   * Get all menus for a canteen on a given date.
   * Used by the canteen manager dashboard.
   *
   * @param canteenId the canteen UUID
   * @param date      the date to query (defaults to today)
   * @return 200 with list of menus, 404 if canteen not found
   */
  @GetMapping
  public ResponseEntity<List<DailyMenuResponse>> getMenus(
      @PathVariable UUID canteenId,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {
    if (canteenService.findById(canteenId).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    List<DailyMenuResponse> menus = dailyMenuService
        .findByCanteenAndDate(canteenId, date)
        .stream()
        .map(this::toMenuResponse)
        .toList();
    return ResponseEntity.ok(menus);
  }

  /**
   * Get today's published menus for all canteens on a campus.
   * Used by students — shows what is available today.
   *
   * @param campusId the opaque campus ID
   * @param date     the date to query (defaults to today)
   * @return 200 with list of published menus
   */
  @GetMapping("/campus")
  public ResponseEntity<List<DailyMenuResponse>> getPublishedMenusByCampus(
      @RequestParam String campusId,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {
    List<DailyMenuResponse> menus = dailyMenuService
        .findPublishedByCampusAndDate(campusId, date)
        .stream()
        .map(this::toMenuResponse)
        .toList();
    return ResponseEntity.ok(menus);
  }

  /**
   * Create a new menu in DRAFT status.
   *
   * @param canteenId the canteen UUID
   * @param request   the menu data
   * @return 200 with the created menu, 404 if canteen not found
   */
  @PostMapping
  public ResponseEntity<DailyMenuResponse> createMenu(
      @PathVariable UUID canteenId,
      @Valid @RequestBody DailyMenuRequest request) {
    Canteen canteen = canteenService.findById(canteenId).orElse(null);
    if (canteen == null) {
      return ResponseEntity.notFound().build();
    }
    DailyMenu menu = dailyMenuService.createIfAbsent(
        canteen, request.getDate(), request.getMealType(), request.getNotes());
    return ResponseEntity.ok(toMenuResponse(menu));
  }

  /**
   * Publish a menu — transitions from DRAFT to PUBLISHED.
   *
   * @param canteenId the canteen UUID
   * @param menuId    the menu UUID
   * @return 200 with updated menu, 404 if not found, 400 if invalid transition
   */
  @PutMapping("/{menuId}/publish")
  public ResponseEntity<DailyMenuResponse> publishMenu(
      @PathVariable UUID canteenId,
      @PathVariable UUID menuId) {
    try {
      DailyMenu menu = dailyMenuService.publish(menuId);
      return ResponseEntity.ok(toMenuResponse(menu));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Close a menu — transitions from PUBLISHED to CLOSED.
   *
   * @param canteenId the canteen UUID
   * @param menuId    the menu UUID
   * @return 200 with updated menu, 404 if not found, 400 if invalid transition
   */
  @PutMapping("/{menuId}/close")
  public ResponseEntity<DailyMenuResponse> closeMenu(
      @PathVariable UUID canteenId,
      @PathVariable UUID menuId) {
    try {
      DailyMenu menu = dailyMenuService.close(menuId);
      return ResponseEntity.ok(toMenuResponse(menu));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // ================================
  // Menu item endpoints
  // ================================

  /**
   * Get all items for a menu.
   *
   * @param menuId the menu UUID
   * @return 200 with list of items, 404 if menu not found
   */
  @GetMapping("/{menuId}/items")
  public ResponseEntity<List<MenuItemResponse>> getItems(@PathVariable UUID menuId) {
    if (dailyMenuService.findById(menuId).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    List<MenuItemResponse> items = menuItemService.findByDailyMenu(menuId)
        .stream()
        .map(this::toItemResponse)
        .toList();
    return ResponseEntity.ok(items);
  }

  /**
   * Add a new item to a menu. Only allowed while menu is DRAFT.
   *
   * @param menuId  the menu UUID
   * @param request the item data
   * @return 200 with created item, 404 if menu not found, 400 if menu not DRAFT
   */
  @PostMapping("/{menuId}/items")
  public ResponseEntity<MenuItemResponse> addItem(
      @PathVariable UUID menuId,
      @Valid @RequestBody MenuItemRequest request) {
    DailyMenu menu = dailyMenuService.findById(menuId).orElse(null);
    if (menu == null) {
      return ResponseEntity.notFound().build();
    }
    try {
      MenuItem item = menuItemService.addItem(menu, toItemEntity(request));
      return ResponseEntity.ok(toItemResponse(item));
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Update a menu item. Only allowed while menu is DRAFT.
   *
   * @param itemId  the item UUID
   * @param request the updated item data
   * @return 200 with updated item, 404 if not found, 400 if menu not DRAFT
   */
  @PutMapping("/{menuId}/items/{itemId}")
  public ResponseEntity<MenuItemResponse> updateItem(
      @PathVariable UUID menuId,
      @PathVariable UUID itemId,
      @Valid @RequestBody MenuItemRequest request) {
    try {
      MenuItem updated = menuItemService.update(itemId, toItemEntity(request));
      return ResponseEntity.ok(toItemResponse(updated));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Toggle availability of a menu item. Allowed at any time.
   *
   * @param itemId    the item UUID
   * @param available the new availability flag
   * @return 200 with updated item, 404 if not found
   */
  @PutMapping("/{menuId}/items/{itemId}/availability")
  public ResponseEntity<MenuItemResponse> setAvailability(
      @PathVariable UUID menuId,
      @PathVariable UUID itemId,
      @RequestParam boolean available) {
    try {
      MenuItem item = menuItemService.setAvailable(itemId, available);
      return ResponseEntity.ok(toItemResponse(item));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // ================================
  // Preference endpoints
  // ================================

  /**
   * Submit a meal preference for a menu item.
   *
   * @param menuId  the menu UUID
   * @param userId  the student's user ID from the gateway header
   * @param request the preference data
   * @return 200 on success, 404 if menu or item not found, 400 if invalid
   */
  @PostMapping("/{menuId}/preferences")
  public ResponseEntity<Void> submitPreference(
      @PathVariable UUID menuId,
      @RequestHeader(USER_ID_HEADER) String userId,
      @Valid @RequestBody PreferenceRequest request) {
    MenuItem menuItem = menuItemService.findById(request.getMenuItemId()).orElse(null);
    if (menuItem == null) {
      return ResponseEntity.notFound().build();
    }
    try {
      menuPreferenceService.submitPreference(userId, menuItem);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get the student's preferences for a specific menu.
   *
   * @param menuId the menu UUID
   * @param userId the student's user ID from the gateway header
   * @return 200 with list of preferred item IDs
   */
  @GetMapping("/{menuId}/preferences/my")
  public ResponseEntity<List<UUID>> getMyPreferences(
      @PathVariable UUID menuId,
      @RequestHeader(USER_ID_HEADER) String userId) {
    List<UUID> itemIds = menuPreferenceService
        .getStudentPreferences(userId, menuId)
        .stream()
        .map(p -> p.getMenuItem().getId())
        .toList();
    return ResponseEntity.ok(itemIds);
  }

  /**
   * Get the demand report for a closed menu.
   * Only available when the menu is CLOSED.
   *
   * @param menuId the menu UUID
   * @return 200 with demand report, 404 if not found, 400 if menu not CLOSED
   */
  @GetMapping("/{menuId}/demand-report")
  public ResponseEntity<DemandReportResponse> getDemandReport(@PathVariable UUID menuId) {
    DailyMenu menu = dailyMenuService.findById(menuId).orElse(null);
    if (menu == null) {
      return ResponseEntity.notFound().build();
    }
    try {
      Map<UUID, Long> counts = menuPreferenceService.getDemandReport(menu);
      DemandReportResponse response = new DemandReportResponse();
      response.setMenuId(menuId);
      response.setCounts(counts);
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // ================================
  // Private mapping helpers
  // ================================

  private DailyMenuResponse toMenuResponse(DailyMenu menu) {
    DailyMenuResponse response = new DailyMenuResponse();
    response.setId(menu.getId());
    response.setCanteenId(menu.getCanteen().getId());
    response.setDate(menu.getDate());
    response.setMealType(menu.getMealType());
    response.setStatus(menu.getStatus());
    response.setNotes(menu.getNotes());
    response.setPublishedAt(menu.getPublishedAt());
    response.setClosedAt(menu.getClosedAt());
    return response;
  }

  private MenuItem toItemEntity(MenuItemRequest request) {
    MenuItem item = new MenuItem();
    item.setName(request.getName());
    item.setDescription(request.getDescription());
    item.setCategory(request.getCategory());
    item.setAllergens(request.getAllergens());
    item.setDietaryTags(request.getDietaryTags());
    return item;
  }

  private MenuItemResponse toItemResponse(MenuItem item) {
    MenuItemResponse response = new MenuItemResponse();
    response.setId(item.getId());
    response.setName(item.getName());
    response.setDescription(item.getDescription());
    response.setCategory(item.getCategory());
    response.setAllergens(item.getAllergens());
    response.setDietaryTags(item.getDietaryTags());
    response.setAvailable(item.isAvailable());
    return response;
  }
}