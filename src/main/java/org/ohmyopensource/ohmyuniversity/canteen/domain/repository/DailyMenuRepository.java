package org.ohmyopensource.ohmyuniversity.canteen.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DailyMenu;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MealType;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link DailyMenu} entities.
 */
@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, UUID> {

  /**
   * Find the menu for a specific canteen, date and meal type.
   * Used to prevent duplicate menus and to retrieve the current menu.
   *
   * @param canteenId the canteen UUID
   * @param date      the menu date
   * @param mealType  LUNCH or DINNER
   * @return the menu if it exists
   */
  Optional<DailyMenu> findByCanteenIdAndDateAndMealType(
      UUID canteenId, LocalDate date, MealType mealType);

  /**
   * Find all published menus for a campus on a given date.
   * Used by students to see what is available today across all canteens.
   *
   * @param campusId the opaque campus ID from the core service
   * @param date     the menu date
   * @param status   the menu status to filter by
   * @return list of matching menus
   */
  @Query("""
      SELECT m FROM DailyMenu m
      JOIN m.canteen c
      WHERE c.campusId = :campusId
        AND m.date = :date
        AND m.status = :status
      """)
  List<DailyMenu> findByCampusIdAndDateAndStatus(
      @Param("campusId") String campusId,
      @Param("date") LocalDate date,
      @Param("status") MenuStatus status);

  /**
   * Find all menus for a canteen on a given date regardless of status.
   * Used by the canteen manager to see the full picture for the day.
   *
   * @param canteenId the canteen UUID
   * @param date      the menu date
   * @return list of all menus for that day
   */
  List<DailyMenu> findByCanteenIdAndDate(UUID canteenId, LocalDate date);
}