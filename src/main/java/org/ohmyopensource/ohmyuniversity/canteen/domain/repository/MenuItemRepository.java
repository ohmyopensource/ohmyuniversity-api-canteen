package org.ohmyopensource.ohmyuniversity.canteen.domain.repository;

import java.util.List;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link MenuItem} entities.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

  /**
   * Find all items for a given daily menu.
   * Used to display the full menu to students and the canteen manager.
   *
   * @param dailyMenuId the daily menu UUID
   * @return list of all items, empty list if none
   */
  List<MenuItem> findByDailyMenuId(UUID dailyMenuId);

  /**
   * Find all available items for a given daily menu.
   * Used to show students only what is currently available.
   *
   * @param dailyMenuId the daily menu UUID
   * @return list of available items, empty list if none
   */
  List<MenuItem> findByDailyMenuIdAndAvailableTrue(UUID dailyMenuId);
}