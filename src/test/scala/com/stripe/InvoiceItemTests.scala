package com.stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

class InvoiceItemTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    def createDefaultInvoiceItem(): InvoiceItem = {
      val customer = Customer.create(DefaultCustomerMap)
      return InvoiceItem.create(DefaultInvoiceItemMap + ("customer" -> customer.id))
    }

    "InvoiceItems can be created" - {
      val invoiceItem = createDefaultInvoiceItem()

      expect(invoiceItem.date > 0L)
    }

    "InvoiceItems can be retrieved individually" - {
      val createdInvoiceItem = createDefaultInvoiceItem()
      val retrievedInvoiceItem = InvoiceItem.retrieve(createdInvoiceItem.id)

      expect(createdInvoiceItem.date == retrievedInvoiceItem.date)
    }

    "InvoiceItems can be updated" - {
      val invoiceItem = createDefaultInvoiceItem()
      val updatedInvoiceItem = invoiceItem.update(Map(
        "amount" -> 200, "description" -> "Updated Scala InvoiceItem"))

      expect {
        updatedInvoiceItem.amount == 200
        updatedInvoiceItem.description.get == "Updated Scala InvoiceItem"
      }
    }

    "InvoiceItems can be deleted" - {
      val invoiceItem = createDefaultInvoiceItem()
      val deletedInvoiceItem = invoiceItem.delete()

      expect(deletedInvoiceItem.deleted == true)
      expect(deletedInvoiceItem.id == invoiceItem.id)
    }

    "InvoiceItems can be listed" - {
      val invoiceItem = createDefaultInvoiceItem()
      val invoiceItems = InvoiceItem.all().data

      expect(invoiceItems.head.isInstanceOf[InvoiceItem])
    }
  }
}
