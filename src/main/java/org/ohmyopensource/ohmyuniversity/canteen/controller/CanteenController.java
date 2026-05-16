package org.ohmyopensource.ohmyuniversity.canteen.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.Canteen;
import org.ohmyopensource.ohmyuniversity.canteen.dto.CanteenRequest;
import org.ohmyopensource.ohmyuniversity.canteen.dto.CanteenResponse;
import org.ohmyopensource.ohmyuniversity.canteen.service.CanteenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for canteen management.
 *
 * POST and PUT endpoints are intended for university administrators.
 * GET endpoints are accessible by all authenticated users.
 *
 * The userId is read from the X-User-Id header forwarded by the gateway.
 */
@RestController
@RequestMapping("/api/canteens")
public class CanteenController {

  private final CanteenService canteenService;

  public CanteenController(CanteenService canteenService) {
    this.canteenService = canteenService;
  }

  /**
   * Get a canteen by ID.
   *
   * @param canteenId the canteen UUID
   * @return 200 with canteen data, 404 if not found
   */
  @GetMapping("/{canteenId}")
  public ResponseEntity<CanteenResponse> getCanteen(@PathVariable UUID canteenId) {
    return canteenService.findById(canteenId)
        .map(this::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Get all active canteens for a campus.
   * Used by students to see which canteens are available on their campus.
   *
   * @param campusId the opaque campus ID
   * @return 200 with list of canteens
   */
  @GetMapping
  public ResponseEntity<List<CanteenResponse>> getCanteensByCampus(
      @RequestParam String campusId) {
    List<CanteenResponse> canteens = canteenService.findActiveByCampus(campusId)
        .stream()
        .map(this::toResponse)
        .toList();
    return ResponseEntity.ok(canteens);
  }

  /**
   * Create a new canteen.
   *
   * @param request the canteen data
   * @return 200 with the created canteen
   */
  @PostMapping
  public ResponseEntity<CanteenResponse> createCanteen(
      @Valid @RequestBody CanteenRequest request) {
    Canteen canteen = toEntity(request);
    Canteen saved = canteenService.create(canteen);
    return ResponseEntity.ok(toResponse(saved));
  }

  /**
   * Update an existing canteen.
   *
   * @param canteenId the canteen UUID
   * @param request   the updated canteen data
   * @return 200 with updated canteen, 404 if not found
   */
  @PutMapping("/{canteenId}")
  public ResponseEntity<CanteenResponse> updateCanteen(
      @PathVariable UUID canteenId,
      @Valid @RequestBody CanteenRequest request) {
    try {
      Canteen updated = canteenService.update(canteenId, toEntity(request));
      return ResponseEntity.ok(toResponse(updated));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // ================================
  // Private mapping helpers
  // ================================

  private Canteen toEntity(CanteenRequest request) {
    Canteen canteen = new Canteen();
    canteen.setCampusId(request.getCampusId());
    canteen.setUniversityId(request.getUniversityId());
    canteen.setName(request.getName());
    canteen.setAddress(request.getAddress());
    canteen.setLunchPreferenceDeadline(request.getLunchPreferenceDeadline());
    canteen.setDinnerPreferenceDeadline(request.getDinnerPreferenceDeadline());
    return canteen;
  }

  private CanteenResponse toResponse(Canteen canteen) {
    CanteenResponse response = new CanteenResponse();
    response.setId(canteen.getId());
    response.setCampusId(canteen.getCampusId());
    response.setUniversityId(canteen.getUniversityId());
    response.setName(canteen.getName());
    response.setAddress(canteen.getAddress());
    response.setLunchPreferenceDeadline(canteen.getLunchPreferenceDeadline());
    response.setDinnerPreferenceDeadline(canteen.getDinnerPreferenceDeadline());
    response.setActive(canteen.isActive());
    return response;
  }
}