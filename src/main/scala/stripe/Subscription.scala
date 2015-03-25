package stripe

case class Subscription(
  plan: Plan,
  start: Long,
  status: String,
  customer: String,
  cancelAtPeriodEnd: Option[Boolean],
  currentPeriodStart: Option[Long],
  currentPeriodEnd: Option[Long],
  endedAt: Option[Long],
  trialStart: Option[Long],
  trialEnd: Option[Long],
  canceledAt: Option[Long]) extends APIResource
