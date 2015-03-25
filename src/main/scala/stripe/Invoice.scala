package stripe

case class InvoiceItem(
  id: String,
  amount: Int,
  currency: String,
  date: Long,
  livemode: Boolean,
  description: Option[String],
  invoice: Option[Invoice]) extends APIResource {
  def update(params: Map[String,_]): InvoiceItem = {
    request("POST", instanceURL(this.id), params).extract[InvoiceItem]
  }

  def delete(): DeletedInvoiceItem = {
    request("DELETE", instanceURL(this.id)).extract[DeletedInvoiceItem]
  }
}

case class DeletedInvoiceItem(id: String, deleted: Boolean)

case class InvoiceItemCollection(count: Int, data: List[InvoiceItem])

object InvoiceItem extends APIResource {
  def create(params: Map[String,_]): InvoiceItem = {
    request("POST", classURL, params).extract[InvoiceItem]
  }

  def retrieve(id: String): InvoiceItem = {
    request("GET", instanceURL(id)).extract[InvoiceItem]
  }

  def all(params: Map[String,_] = Map.empty): InvoiceItemCollection = {
    request("GET", classURL, params).extract[InvoiceItemCollection]
  }
}

case class InvoiceLineSubscriptionPeriod(start: Long, end: Long)
case class InvoiceLineSubscription(plan: Plan, amount: Int, period: InvoiceLineSubscriptionPeriod)
case class InvoiceLines(
  subscriptions: List[InvoiceLineSubscription],
  invoiceItems: List[InvoiceItem],
  prorations: List[InvoiceItem]) extends APIResource {
}

case class Invoice(
  date: Long,
  // id is optional since UpcomingInvoices don't have an ID.
  id: Option[String],
  periodStart: Long,
  periodEnd: Long,
  lines: InvoiceLines,
  subtotal: Int,
  total: Int,
  customer: String,
  attempted: Boolean,
  closed: Boolean,
  paid: Boolean,
  livemode: Boolean,
  attemptCount: Int,
  amountDue: Int,
  startingBalance: Int,
  endingBalance: Option[Int],
  nextPaymentAttempt: Option[Long],
  charge: Option[String],
  discount: Option[Discount]) {
}

case class InvoiceCollection(count: Int, data: List[Invoice])

object Invoice extends APIResource {
  def retrieve(id: String): Invoice = {
    request("GET", instanceURL(id)).extract[Invoice]
  }

  def all(params: Map[String,_] = Map.empty): InvoiceCollection = {
    request("GET", classURL, params).extract[InvoiceCollection]
  }

  def upcoming(params: Map[String, _]): Invoice = {
    request("GET", "%s/upcoming".format(classURL), params).extract[Invoice]
  }
}
