package com.stripe

case class CardCollection(count: Int, data: List[Card])

case class Card(
  last4: String,
  `type`: String,
  expMonth: Int,
  expYear: Int,
  fingerprint: String,
  country: Option[String],
  name: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine2: Option[String] = None,
  addressZip: Option[String] = None,
  addressState: Option[String] = None,
  addressCountry: Option[String] = None,
  cvcCheck: Option[String] = None,
  addressLine1Check: Option[String] = None,
  addressZipCheck: Option[String] = None) extends APIResource
