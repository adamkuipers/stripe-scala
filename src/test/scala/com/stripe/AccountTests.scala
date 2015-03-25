package com.stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

object AccountTests extends TestSuite {
  import fixtures._

  val tests = TestSuite {
    val expect = new Expecty()

    "Account can be retrieved" - {
      val account = Account.retrieve

      /* expect {
        account.chargeEnabled == true
        account.detailsSubmitted == true
        account.statementDescriptor.isEmpty
        account.currenciesSupported.length == 1
        account.currenciesSupported.head == "USD"
      }*/
    }
  }
}
