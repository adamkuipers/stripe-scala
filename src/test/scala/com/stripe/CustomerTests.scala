package com.stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

object CustomerTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    "Customers can be created" - {
      val customer = Customer.create(DefaultCustomerMap + ("description" -> "Test Description"))

      expect {
        customer.description.get == "Test Description"
        !customer.defaultCard.isEmpty
      }
    }

    "Customers can be retrieved individually" - {
      val createdCustomer = Customer.create(DefaultCustomerMap)
      val retrievedCustomer = Customer.retrieve(createdCustomer.id)

      expect(createdCustomer.created == retrievedCustomer.created)
    }

    "Customers can be updated" - {
      val customer = Customer.create(DefaultCustomerMap)
      val updatedCustomer = customer.update(Map("description" -> "Updated Scala Customer"))

      updatedCustomer.description.get == "Updated Scala Customer"
    }

    "Customers can be deleted" - {
      val customer = Customer.create(DefaultCustomerMap)
      val deletedCustomer = customer.delete()

      expect(deletedCustomer.deleted)
      expect(deletedCustomer.id == customer.id)
    }

    "Customers can be listed" - {
      val customer = Customer.create(DefaultCustomerMap)
      val customers = Customer.all().data

      expect(customers.head.isInstanceOf[Customer])
    }
  }
}
