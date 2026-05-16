package org.ohmyopensource.ohmyuniversity.canteen.domain.repository;

import java.util.List;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.Canteen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Canteen} entities.
 */
@Repository
public interface CanteenRepository extends JpaRepository<Canteen, UUID> {

  /**
   * Find all active canteens for a given campus.
   * Used to show students which canteens are available on their campus.
   *
   * @param campusId opaque campus ID from the core service
   * @return list of active canteens, empty list if none
   */
  List<Canteen> findByCampusIdAndActiveTrue(String campusId);

  /**
   * Find all active canteens for a given university.
   * Used by the canteen manager dashboard.
   *
   * @param universityId opaque university ID from the core service
   * @return list of active canteens, empty list if none
   */
  List<Canteen> findByUniversityIdAndActiveTrue(String universityId);
}