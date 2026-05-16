package org.ohmyopensource.ohmyuniversity.canteen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DailyMenu;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItem;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItemPreference;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuStatus;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.StudentCampusAssignment;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.MenuItemPreferenceRepository;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.StudentCampusAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing student meal preferences and demand reports.
 *
 * A student can submit preferences for multiple items in the same menu
 * (e.g. a primo and a secondo). Preferences are immutable once submitted.
 *
 * The demand report aggregates preference counts per menu item,
 * allowing the canteen manager to estimate quantities to prepare.
 */
@Service
@Transactional(readOnly = true)
public class MenuPreferenceService {

  private static final Logger log = LoggerFactory.getLogger(MenuPreferenceService.class);

  private final MenuItemPreferenceRepository preferenceRepository;
  private final StudentCampusAssignmentRepository studentCampusAssignmentRepository;

  public MenuPreferenceService(
      MenuItemPreferenceRepository preferenceRepository,
      StudentCampusAssignmentRepository studentCampusAssignmentRepository) {
    this.preferenceRepository = preferenceRepository;
    this.studentCampusAssignmentRepository = studentCampusAssignmentRepository;
  }

  /**
   * Submit a preference for a menu item.
   *
   * <p>Idempotent — if the student has already expressed a preference
   * for this item, returns the existing preference silently.
   *
   * <p>Validates that:
   * <ul>
   *   <li>The parent menu is in PUBLISHED status</li>
   *   <li>The menu item is available</li>
   *   <li>The student belongs to the same campus as the canteen</li>
   * </ul>
   *
   * @param studentId the opaque student ID from the gateway header
   * @param menuItem  the menu item to express preference for
   * @return the persisted preference (existing or newly created)
   * @throws IllegalStateException if the menu is not PUBLISHED,
   *                               if the item is unavailable,
   *                               or if the student is not on the correct campus
   */
  @Transactional
  public MenuItemPreference submitPreference(String studentId, MenuItem menuItem) {
    DailyMenu menu = menuItem.getDailyMenu();

    if (menu.getStatus() != MenuStatus.PUBLISHED) {
      throw new IllegalStateException(
          "Cannot submit preferences for a menu with status: " + menu.getStatus());
    }

    if (!menuItem.isAvailable()) {
      throw new IllegalStateException(
          "Menu item is not available: " + menuItem.getId());
    }

    log.debug("Validating student campus: studentId={} requiredCampusId={}",
        studentId, menuItem.getDailyMenu().getCanteen().getCampusId());
    validateStudentCampus(studentId, menu.getCanteen().getCampusId());

    if (preferenceRepository.existsByStudentIdAndMenuItemId(studentId, menuItem.getId())) {
      return preferenceRepository
          .findByStudentIdAndDailyMenuId(studentId, menu.getId())
          .stream()
          .filter(p -> p.getMenuItem().getId().equals(menuItem.getId()))
          .findFirst()
          .orElseThrow();
    }

    MenuItemPreference preference = new MenuItemPreference();
    preference.setStudentId(studentId);
    preference.setMenuItem(menuItem);
    MenuItemPreference saved = preferenceRepository.save(preference);

    log.debug("Preference submitted: studentId={} menuItemId={}",
        studentId, menuItem.getId());

    return saved;
  }

  /**
   * Get all preferences a student has submitted for a specific menu.
   *
   * @param studentId   the opaque student ID
   * @param dailyMenuId the daily menu UUID
   * @return list of preferences for that menu
   */
  public List<MenuItemPreference> getStudentPreferences(
      String studentId, UUID dailyMenuId) {
    return preferenceRepository.findByStudentIdAndDailyMenuId(studentId, dailyMenuId);
  }

  /**
   * Generate the demand report for a daily menu.
   * Returns a map of menuItemId → preference count.
   *
   * <p>Only available once the menu is CLOSED.
   *
   * @param menu the daily menu
   * @return map of menuItemId to preference count
   * @throws IllegalStateException if the menu is not CLOSED
   */
  public Map<UUID, Long> getDemandReport(DailyMenu menu) {
    if (menu.getStatus() != MenuStatus.CLOSED) {
      throw new IllegalStateException(
          "Demand report is only available for CLOSED menus. Current status: "
              + menu.getStatus());
    }

    List<Object[]> rows = preferenceRepository.countByMenuItemForDailyMenu(menu.getId());
    Map<UUID, Long> report = new HashMap<>();
    for (Object[] row : rows) {
      UUID menuItemId = (UUID) row[0];
      Long count = (Long) row[1];
      report.put(menuItemId, count);
    }
    return report;
  }

  // ================================
  // Private helpers
  // ================================

  private void validateStudentCampus(String studentId, String requiredCampusId) {
    StudentCampusAssignment assignment = studentCampusAssignmentRepository
        .findByStudentId(studentId)
        .orElseThrow(() -> new IllegalStateException(
            "Student has no campus assignment: " + studentId));

    if (!assignment.getCampusId().equals(requiredCampusId)) {
      throw new IllegalStateException(
          "Student campus does not match canteen campus: studentId=" + studentId);
    }

    log.debug("Student assignment found: campusId={}", assignment.getCampusId());
  }
}