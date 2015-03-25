package com.stripe

import java.util.UUID

package object fixtures {
  apiKey = sys.env("API_KEY")

  val DefaultCardMap = Map[String, Any](
    "name" -> "Scala User",
    "cvc" -> "100",
    "address_line1" -> "12 Main Street",
    "address_line2" -> "Palo Alto",
    "address_zip" -> "94105",
    "address_country" -> "USA",
    "number" -> "4242424242424242",
    "exp_month" -> 3,
    "exp_year" -> 2015)

  val DefaultChargeMap = Map[String, Any]("amount" -> 100, "currency" -> "usd", "card" -> DefaultCardMap)

  val DefaultCustomerMap = Map[String, Any]("description" -> "Scala Customer", "card" -> DefaultCardMap)

  val DefaultPlanMap = Map[String, Any]("amount" -> 100, "currency" -> "usd", "interval" -> "month", "name" -> "Scala Plan")

  def getUniquePlanId(): String = return "PLAN-%s".format(UUID.randomUUID())

  def getUniquePlanMap(): Map[String,_] = return DefaultPlanMap + ("id" -> getUniquePlanId())

  val DefaultInvoiceItemMap = Map[String, Any]("amount" -> 100, "currency" -> "usd")

  def getUniqueCouponMap(): Map[String,_] = Map("id" -> "COUPON-%s".format(UUID.randomUUID()),
    "duration" -> "once",
    "percent_off" -> 10
  )
}
