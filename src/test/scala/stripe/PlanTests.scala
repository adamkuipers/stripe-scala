package stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

object PlanTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    "Plans can be created" - {
      val plan = Plan.create(getUniquePlanMap + ("interval" -> "year"))

      expect(plan.interval == "year")
    }

    "Plans can be retrieved individually" - {
      val createdPlan = Plan.create(getUniquePlanMap)
      val retrievedPlan = Plan.retrieve(createdPlan.id)

      expect(createdPlan == retrievedPlan)
    }

    "Plans can be deleted" - {
      val plan = Plan.create(getUniquePlanMap)
      val deletedPlan = plan.delete()

      expect(deletedPlan.deleted)
      expect(deletedPlan.id == plan.id)
    }

    "Plans can be listed" - {
      val plan = Plan.create(getUniquePlanMap)
      val plans = Plan.all().data

      expect(plans.head.isInstanceOf[Plan])
    }

    "Customers can be created with a plan" - {
      val plan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> plan.id))

      expect(customer.subscription.get.plan.id == plan.id)
    }

    "A plan can be added to a customer without a plan" - {
      val customer = Customer.create(DefaultCustomerMap)
      val plan = Plan.create(getUniquePlanMap)
      val subscription = customer.updateSubscription(Map("plan" -> plan.id))

      expect {
        subscription.customer == customer.id
        subscription.plan.id == plan.id
      }
    }

    "A customer's existing plan can be replaced" - {
      val origPlan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> origPlan.id))
      val newPlan = Plan.create(getUniquePlanMap)
      val subscription = customer.updateSubscription(Map("plan" -> newPlan.id))
      val updatedCustomer = Customer.retrieve(customer.id)

      expect {
        customer.subscription.get.plan.id == origPlan.id
        updatedCustomer.subscription.get.plan.id == newPlan.id
      }
    }

    "Customer subscriptions can be canceled" - {
      val plan = Plan.create(getUniquePlanMap)
      val customer = Customer.create(DefaultCustomerMap + ("plan" -> plan.id))
      val canceledSubscription = customer.cancelSubscription()

      expect {
        customer.subscription.get.status == "active"
        canceledSubscription.status == "canceled"
      }
    }
  }
}
