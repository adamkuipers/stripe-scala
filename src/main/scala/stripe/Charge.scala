package stripe

case class Charge(
  created: Long,
  id: String,
  livemode: Boolean,
  paid: Boolean,
  amount: Int,
  currency: String,
  refunded: Boolean,
  disputed: Boolean,
  fee: Int,
  card: Card,
  failureMessage: Option[String],
  amountRefunded: Option[Int],
  customer: Option[String],
  invoice: Option[String],
  description: Option[String]) extends APIResource {
  def refund(): Charge = request("POST", "%s/refund".format(instanceURL(this.id))).extract[Charge]
}

case class ChargeCollection(count: Int, data: List[Charge])

object Charge extends APIResource {
  def create(params: Map[String,_]): Charge = {
    request("POST", classURL, params).extract[Charge]
  }

  def retrieve(id: String): Charge = {
    request("GET", instanceURL(id)).extract[Charge]
  }

  def all(params: Map[String,_] = Map.empty): ChargeCollection = {
    request("GET", classURL, params).extract[ChargeCollection]
  }

}
