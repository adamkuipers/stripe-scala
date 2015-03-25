package stripe

case class Discount(
  id: String,
  coupon: String,
  start: Long,
  customer: String,
  end: Option[Long]) extends APIResource
