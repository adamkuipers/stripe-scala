package stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

object TokenTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    "Tokens can be created" - {
      val token = Token.create(Map("card" -> DefaultCardMap))

      expect(!token.used)
    }

    "Tokens can be retrieved" - {
      val createdToken = Token.create(Map("card" -> DefaultCardMap))
      val retrievedToken = Token.retrieve(createdToken.id)

      expect(createdToken.created == retrievedToken.created)
    }

    "Tokens can be used" - {
      val createdToken = Token.create(Map("card" -> DefaultCardMap))
      val charge = Charge.create(Map("amount" -> 100, "currency" -> "usd", "card" -> createdToken.id))
      val retrievedToken = Token.retrieve(createdToken.id)

      expect(!createdToken.used)
      expect(retrievedToken.used)
    }
  }
}
