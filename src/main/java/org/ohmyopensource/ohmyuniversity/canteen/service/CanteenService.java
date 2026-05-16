package org.ohmyopensource.ohmyuniversity.canteen.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.Canteen;
import org.ohmyopensource.ohmyuniversity.canteen.domain.repository.CanteenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link Canteen} entities.
 *
 * Canteens are created and managed by university administrators
 * via REST endpoints.
 */
@Service
@Transactional(readOnly = true)
public class CanteenService {

  private final CanteenRepository canteenRepository;

  public CanteenService(CanteenRepository canteenRepository) {
    this.canteenRepository = canteenRepository;
  }

  /**
   * Find a canteen by its internal UUID.
   *
   * @param canteenId the canteen UUID
   * @return the canteen if found
   */
  public Optional<Canteen> findById(UUID canteenId) {
    return canteenRepository.findById(canteenId);
  }

  /**
   * Find all active canteens for a given campus.
   * Used by students to see which canteens are available on their campus.
   *
   * @param campusId opaque campus ID from the core service
   * @return list of active canteens, empty list if none
   */
  public List<Canteen> findActiveByCampus(String campusId) {
    return canteenRepository.findByCampusIdAndActiveTrue(campusId);
  }

  /**
   * Find all active canteens for a given university.
   * Used by the canteen manager dashboard.
   *
   * @param universityId opaque university ID from the core service
   * @return list of active canteens, empty list if none
   */
  public List<Canteen> findActiveByUniversity(String universityId) {
    return canteenRepository.findByUniversityIdAndActiveTrue(universityId);
  }

  /**
   * Create a new canteen.
   *
   * @param canteen the canteen to create (id must be null)
   * @return the persisted canteen
   */
  @Transactional
  public Canteen create(Canteen canteen) {
    return canteenRepository.save(canteen);
  }

  /**
   * Update an existing canteen.
   *
   * @param canteenId the canteen UUID
   * @param updated   the canteen with updated fields
   * @return the updated canteen
   * @throws IllegalArgumentException if the canteen does not exist
   */
  @Transactional
  public Canteen update(UUID canteenId, Canteen updated) {
    Canteen existing = canteenRepository.findById(canteenId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Canteen not found: " + canteenId));

    existing.setName(updated.getName());
    existing.setAddress(updated.getAddress());
    existing.setLunchPreferenceDeadline(updated.getLunchPreferenceDeadline());
    existing.setDinnerPreferenceDeadline(updated.getDinnerPreferenceDeadline());
    existing.setActive(updated.isActive());
    return canteenRepository.save(existing);
  }
}