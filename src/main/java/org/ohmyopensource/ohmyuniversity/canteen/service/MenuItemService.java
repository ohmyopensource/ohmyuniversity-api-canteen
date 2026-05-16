package org.ohmyopensource.ohmyuniversity.canteen.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.DailyMenu;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuStatus;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MenuItem;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link MenuItem} entities.
 *
 * Menu items can only be added or modified while the parent menu
 * is in DRAFT status. Once published, items are immutable except
 * for the {@code available} flag which can be toggled at any time.
 */
@Service
@Transactional(readOnly = true)
public class MenuItemService {

  private final MenuItemRepository menuItemRepository;

  public MenuItemService(MenuItemRepository menuItemRepository) {
    this.menuItemRepository = menuItemRepository;
  }

  /**
   * Find a menu item by its internal UUID.
   *
   * @param itemId the menu item UUID
   * @return the item if found
   */
  public Optional<MenuItem> findById(UUID itemId) {
    return menuItemRepository.findById(itemId);
  }

  /**
   * Find all items for a given daily menu.
   *
   * @param dailyMenuId the daily menu UUID
   * @return list of all items
   */
  public List<MenuItem> findByDailyMenu(UUID dailyMenuId) {
    return menuItemRepository.findByDailyMenuId(dailyMenuId);
  }

  /**
   * Find only available items for a given daily menu.
   * Used by students — hides items marked unavailable by the canteen manager.
   *
   * @param dailyMenuId the daily menu UUID
   * @return list of available items
   */
  public List<MenuItem> findAvailableByDailyMenu(UUID dailyMenuId) {
    return menuItemRepository.findByDailyMenuIdAndAvailableTrue(dailyMenuId);
  }

  /**
   * Add a new item to a menu.
   * Only allowed while the menu is in DRAFT status.
   *
   * @param menu the parent menu
   * @param item the item to add (id must be null)
   * @return the persisted item
   * @throws IllegalStateException if the menu is not in DRAFT status
   */
  @Transactional
  public MenuItem addItem(DailyMenu menu, MenuItem item) {
    if (menu.getStatus() != MenuStatus.DRAFT) {
      throw new IllegalStateException(
          "Cannot add items to a menu with status: " + menu.getStatus());
    }
    item.setDailyMenu(menu);
    return menuItemRepository.save(item);
  }

  /**
   * Update an existing menu item.
   * Only allowed while the parent menu is in DRAFT status.
   *
   * @param itemId  the item UUID
   * @param updated the item with updated fields
   * @return the updated item
   * @throws IllegalArgumentException if the item does not exist
   * @throws IllegalStateException    if the parent menu is not in DRAFT status
   */
  @Transactional
  public MenuItem update(UUID itemId, MenuItem updated) {
    MenuItem existing = menuItemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Menu item not found: " + itemId));

    if (existing.getDailyMenu().getStatus() != MenuStatus.DRAFT) {
      throw new IllegalStateException(
          "Cannot update items of a menu with status: "
              + existing.getDailyMenu().getStatus());
    }

    existing.setName(updated.getName());
    existing.setDescription(updated.getDescription());
    existing.setCategory(updated.getCategory());
    existing.setAllergens(updated.getAllergens());
    existing.setDietaryTags(updated.getDietaryTags());
    existing.setAvailable(updated.isAvailable());
    return menuItemRepository.save(existing);
  }

  /**
   * Toggle the availability of a menu item.
   * Allowed at any time regardless of menu status — useful when a dish
   * runs out during service.
   *
   * @param itemId    the item UUID
   * @param available the new availability flag
   * @return the updated item
   * @throws IllegalArgumentException if the item does not exist
   */
  @Transactional
  public MenuItem setAvailable(UUID itemId, boolean available) {
    MenuItem item = menuItemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Menu item not found: " + itemId));
    item.setAvailable(available);
    return menuItemRepository.save(item);
  }
}