package org.ohmyopensource.ohmyuniversity.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

/**
 * Request DTO for creating or updating a canteen.
 * Used by POST /api/canteens and PUT /api/canteens/{canteenId}.
 */
public class CanteenRequest {

  @NotBlank
  private String campusId;

  @NotBlank
  private String universityId;

  @NotBlank
  private String name;

  private String address;

  private LocalTime lunchPreferenceDeadline;

  private LocalTime dinnerPreferenceDeadline;

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
}