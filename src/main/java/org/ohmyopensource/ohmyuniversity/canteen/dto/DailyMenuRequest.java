package org.ohmyopensource.ohmyuniversity.canteen.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.ohmyopensource.ohmyuniversity.canteen.domain.entity.MealType;

/**
 * Request DTO for creating a daily menu.
 * Used by POST /api/canteens/{canteenId}/menus.
 */
public class DailyMenuRequest {

  @NotNull
  private LocalDate date;

  @NotNull
  private MealType mealType;

  private String notes;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public MealType getMealType() {
    return mealType;
  }

  public void setMealType(MealType mealType) {
    this.mealType = mealType;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}