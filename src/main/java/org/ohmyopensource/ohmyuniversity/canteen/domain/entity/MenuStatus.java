package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

/**
 * Lifecycle status of a daily menu.
 *
 * Transitions:
 * DRAFT -> PUBLISHED (canteen manager publishes the menu)
 * PUBLISHED -> CLOSED (preference window closes, demand report available)
 */
public enum MenuStatus {

  /** Menu is being prepared. Not visible to students. */
  DRAFT,

  /** Menu is published. Students can submit preferences. */
  PUBLISHED,

  /** Preference window is closed. Demand report is available. */
  CLOSED
}