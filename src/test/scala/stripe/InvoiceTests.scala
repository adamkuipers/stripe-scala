package stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

object InvoiceTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    "Invoices can be retrieved individually" - {
      val plan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> plan.id))
      val invoices = Invoice.all(Map("customer" -> customer.id)).data
      val createdInvoice = invoices.head
      val retrievedInvoice = Invoice.retrieve(createdInvoice.id.get)

      expect(retrievedInvoice.id == createdInvoice.id)
    }

    "Invoices can be listed" - {
      val plan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> plan.id))
      val invoices = Invoice.all().data

      expect(invoices.head.isInstanceOf[Invoice])
    }

    "Invoices can be retrieved for a customer" - {
      val plan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> plan.id))
      val invoices = Invoice.all(Map("customer" -> customer.id)).data
      val invoice = invoices.head
      val invoiceLineSubscription = invoice.lines.subscriptions.head

      expect {
        invoice.customer == customer.id
        invoiceLineSubscription.plan.id == plan.id
      }
    }

    "Upcoming Invoices can be retrieved" - {
      val customer = Customer.create(DefaultCustomerMap)
      val customerId = customer.id
      val invoiceItem = InvoiceItem.create(DefaultInvoiceItemMap + ("customer" -> customerId))
      val upcomingInvoice = Invoice.upcoming(Map("customer" -> customerId))

      upcomingInvoice.attempted == false
    }
  }
}
