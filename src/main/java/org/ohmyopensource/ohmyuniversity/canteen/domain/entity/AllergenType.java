package org.ohmyopensource.ohmyuniversity.canteen.domain.entity;

/**
 * The 14 major allergens as defined by EU Regulation No 1169/2011
 * on the provision of food information to consumers.
 *
 * Mandatory disclosure is required for all food services in Italy
 * and across the European Union.
 */
public enum AllergenType {

  /** Cereals containing gluten (wheat, rye, barley, oats, etc.). */
  GLUTEN,

  /** Crustaceans and crustacean products. */
  CRUSTACEANS,

  /** Eggs and egg products. */
  EGGS,

  /** Fish and fish products. */
  FISH,

  /** Peanuts and peanut products. */
  PEANUTS,

  /** Soybeans and soy products. */
  SOYBEANS,

  /** Milk and dairy products (including lactose). */
  MILK,

  /** Nuts (almonds, hazelnuts, walnuts, cashews, pecans, pistachios, etc.). */
  NUTS,

  /** Celery and celery products. */
  CELERY,

  /** Mustard and mustard products. */
  MUSTARD,

  /** Sesame seeds and sesame seed products. */
  SESAME,

  /** Sulphur dioxide and sulphites (concentrations above 10 mg/kg or 10 mg/l). */
  SULPHITES,

  /** Lupin and lupin products. */
  LUPIN,

  /** Molluscs and mollusc products. */
  MOLLUSCS
}