package com.stripe

case class Account(
  id: String,
  email: Option[String],
  statementDescriptor: Option[String],
  detailsSubmitted: Boolean,
  chargeEnabled: Boolean,
  currenciesSupported: Array[String]
) extends APIResource

object Account extends APIResource {
  def retrieve: Account = {
    request("GET", singleInstanceURL).extract[Account]
  }
}
