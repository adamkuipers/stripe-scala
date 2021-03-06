package stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

object ChargeTests extends TestSuite {
  import fixtures._

  val tests = TestSuite {
    val expect = new Expecty()

    "Charges can be created" - {
      val charge = Charge.create(
        Map(
          "amount" -> 100,
          "currency" -> "usd",
          "card" -> DefaultCardMap))

      expect(!charge.refunded)
    }

    "Charges can be retrieved individually" - {
      val createdCharge = Charge.create(DefaultChargeMap)
      val retrievedCharge = Charge.retrieve(createdCharge.id)

      expect(createdCharge.created == retrievedCharge.created)
    }

    "Charges can be refunded" - {
      val charge = Charge.create(DefaultChargeMap)
      val refundedCharge = charge.refund()

      expect(refundedCharge.refunded)
    }

    "Charges can be listed" - {
      val charge = Charge.create(DefaultChargeMap)
      val charges = Charge.all().data

      expect(charges.head.isInstanceOf[Charge])
    }

    "Invalid card raises CardException" - {
      val e = intercept[CardException] {
        Charge.create(Map(
          "amount" -> 100,
          "currency" -> "usd",
          "card" -> Map("number" -> "4242424242424241", "exp_month" -> 3, "exp_year" -> 2015)
        ))
      }

      expect(e.param.get == "number")
    }

    "CVC, address and zip checks should pass in testmode" - {
      val charge = Charge.create(DefaultChargeMap)

      expect {
        charge.card.cvcCheck.get == "pass"
        charge.card.addressLine1Check.get == "pass"
        charge.card.addressZipCheck.get == "pass"
      }
    }
  }
}
