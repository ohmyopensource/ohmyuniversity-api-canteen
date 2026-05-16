package org.ohmyopensource.ohmyuniversity.canteen.domain.repository;

import java.util.List;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItemPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link MenuItemPreference} entities.
 */
@Repository
public interface MenuItemPreferenceRepository
    extends JpaRepository<MenuItemPreference, UUID> {

  /**
   * Check whether a student has already expressed a preference for a menu item.
   * Used for idempotency — prevents duplicate submissions.
   *
   * @param studentId  the opaque student ID from the gateway header
   * @param menuItemId the menu item UUID
   * @return true if the preference already exists
   */
  boolean existsByStudentIdAndMenuItemId(String studentId, UUID menuItemId);

  /**
   * Find all preferences a student has submitted for items in a specific menu.
   * Used to show the student their own selections.
   *
   * @param studentId   the opaque student ID
   * @param dailyMenuId the daily menu UUID
   * @return list of preferences for that menu
   */
  @Query("""
      SELECT p FROM MenuItemPreference p
      JOIN p.menuItem i
      WHERE p.studentId = :studentId
        AND i.dailyMenu.id = :dailyMenuId
      """)
  List<MenuItemPreference> findByStudentIdAndDailyMenuId(
      @Param("studentId") String studentId,
      @Param("dailyMenuId") UUID dailyMenuId);

  /**
   * Count preferences per menu item for a given daily menu.
   * Used to generate the demand report for the canteen manager.
   * Returns pairs of (menuItemId, count).
   *
   * @param dailyMenuId the daily menu UUID
   * @return list of Object[] where [0] is UUID menuItemId and [1] is Long count
   */
  @Query("""
      SELECT p.menuItem.id, COUNT(p)
      FROM MenuItemPreference p
      WHERE p.menuItem.dailyMenu.id = :dailyMenuId
      GROUP BY p.menuItem.id
      """)
  List<Object[]> countByMenuItemForDailyMenu(@Param("dailyMenuId") UUID dailyMenuId);
}