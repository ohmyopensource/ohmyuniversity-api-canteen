package org.ohmyopensource.ohmyuniversity.canteen.dto;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for GET /api/canteens and related endpoints.
 */
public class CanteenResponse {

  private UUID id;
  private String campusId;
  private String universityId;
  private String name;
  private String address;
  private LocalTime lunchPreferenceDeadline;
  private LocalTime dinnerPreferenceDeadline;
  private boolean active;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCampusId() {
    return campusId;
  }

  public void setCampusId(String campusId) {
    this.campusId = campusId;
  }

  public String getUniversityId() {
    return universityId;
  }

  public void setUniversityId(String universityId) {
    this.universityId = universityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalTime getLunchPreferenceDeadline() {
    return lunchPreferenceDeadline;
  }

  public void setLunchPreferenceDeadline(LocalTime lunchPreferenceDeadline) {
    this.lunchPreferenceDeadline = lunchPreferenceDeadline;
  }

  public LocalTime getDinnerPreferenceDeadline() {
    return dinnerPreferenceDeadline;
  }

  public void setDinnerPreferenceDeadline(LocalTime dinnerPreferenceDeadline) {
    this.dinnerPreferenceDeadline = dinnerPreferenceDeadline;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}