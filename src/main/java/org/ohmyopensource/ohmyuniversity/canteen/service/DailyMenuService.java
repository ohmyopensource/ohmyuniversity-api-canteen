package org.ohmyopensource.ohmyuniversity.canteen.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.Canteen;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DailyMenu;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MealType;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuStatus;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.DailyMenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link DailyMenu} lifecycle.
 *
 * Menus are created by the canteen manager as DRAFT, published
 * to make them visible to students, and closed when the preference
 * window ends.
 */
@Service
@Transactional(readOnly = true)
public class DailyMenuService {

  private static final Logger log = LoggerFactory.getLogger(DailyMenuService.class);

  private final DailyMenuRepository dailyMenuRepository;

  public DailyMenuService(DailyMenuRepository dailyMenuRepository) {
    this.dailyMenuRepository = dailyMenuRepository;
  }

  /**
   * Find a menu by its internal UUID.
   *
   * @param menuId the menu UUID
   * @return the menu if found
   */
  public Optional<DailyMenu> findById(UUID menuId) {
    return dailyMenuRepository.findById(menuId);
  }

  /**
   * Find the menu for a specific canteen, date and meal type.
   *
   * @param canteenId the canteen UUID
   * @param date      the menu date
   * @param mealType  LUNCH or DINNER
   * @return the menu if found
   */
  public Optional<DailyMenu> findByCanteenAndDateAndMealType(
      UUID canteenId, LocalDate date, MealType mealType) {
    return dailyMenuRepository.findByCanteenIdAndDateAndMealType(canteenId, date, mealType);
  }

  /**
   * Find all published menus for a campus on a given date.
   * Used by students to see today's menus across all canteens on their campus.
   *
   * @param campusId the opaque campus ID
   * @param date     the date to query
   * @return list of published menus
   */
  public List<DailyMenu> findPublishedByCampusAndDate(String campusId, LocalDate date) {
    return dailyMenuRepository.findByCampusIdAndDateAndStatus(
        campusId, date, MenuStatus.PUBLISHED);
  }

  /**
   * Find all menus for a canteen on a given date.
   * Used by the canteen manager to see the full picture for the day.
   *
   * @param canteenId the canteen UUID
   * @param date      the date to query
   * @return list of all menus for that day
   */
  public List<DailyMenu> findByCanteenAndDate(UUID canteenId, LocalDate date) {
    return dailyMenuRepository.findByCanteenIdAndDate(canteenId, date);
  }

  /**
   * Create a new menu in DRAFT status.
   *
   * <p>Idempotent — if a menu for the same canteen, date and meal type
   * already exists, returns the existing one.
   *
   * @param canteen  the parent canteen
   * @param date     the menu date
   * @param mealType LUNCH or DINNER
   * @param notes    optional notes from the canteen manager
   * @return the persisted menu (existing or newly created)
   */
  @Transactional
  public DailyMenu createIfAbsent(
      Canteen canteen, LocalDate date, MealType mealType, String notes) {
    return dailyMenuRepository
        .findByCanteenIdAndDateAndMealType(canteen.getId(), date, mealType)
        .orElseGet(() -> {
          DailyMenu menu = new DailyMenu();
          menu.setCanteen(canteen);
          menu.setDate(date);
          menu.setMealType(mealType);
          menu.setStatus(MenuStatus.DRAFT);
          menu.setNotes(notes);
          return dailyMenuRepository.save(menu);
        });
  }

  /**
   * Publish a menu — transitions from DRAFT to PUBLISHED.
   * Students can now see the menu and submit preferences.
   *
   * @param menuId the menu UUID
   * @return the updated menu
   * @throws IllegalArgumentException if the menu does not exist
   * @throws IllegalStateException    if the menu is not in DRAFT status
   */
  @Transactional
  public DailyMenu publish(UUID menuId) {
    DailyMenu menu = dailyMenuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));

    if (menu.getStatus() != MenuStatus.DRAFT) {
      throw new IllegalStateException(
          "Cannot publish menu with status: " + menu.getStatus());
    }

    menu.setStatus(MenuStatus.PUBLISHED);
    menu.setPublishedAt(Instant.now());
    DailyMenu saved = dailyMenuRepository.save(menu);

    log.info("Menu published: menuId={} canteenId={} date={} mealType={}",
        saved.getId(), saved.getCanteen().getId(), saved.getDate(), saved.getMealType());

    return saved;
  }

  /**
   * Close a menu — transitions from PUBLISHED to CLOSED.
   * The preference window ends and the demand report becomes available.
   *
   * @param menuId the menu UUID
   * @return the updated menu
   * @throws IllegalArgumentException if the menu does not exist
   * @throws IllegalStateException    if the menu is not in PUBLISHED status
   */
  @Transactional
  public DailyMenu close(UUID menuId) {
    DailyMenu menu = dailyMenuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));

    if (menu.getStatus() != MenuStatus.PUBLISHED) {
      throw new IllegalStateException(
          "Cannot close menu with status: " + menu.getStatus());
    }

    menu.setStatus(MenuStatus.CLOSED);
    menu.setClosedAt(Instant.now());
    DailyMenu saved = dailyMenuRepository.save(menu);

    log.info("Menu closed: menuId={} canteenId={} date={} mealType={}",
        saved.getId(), saved.getCanteen().getId(), saved.getDate(), saved.getMealType());

    return saved;
  }
}