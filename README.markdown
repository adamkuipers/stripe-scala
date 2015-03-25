= Stripe Scala bindings

== Installation
The current release is distributed for <b>Scala 2.11.0 or later</b>. Add +stripe-scala+ as a dependency in sbt or Maven:

== Run the test suite

Set API_KEY environment variable to your stripe test secret key before running tests.

== Documentation
=== Example

    import com.stripe
    stripe.apiKey = "sSs57dBsUSkxo3lQPsNKNDX5H0RAcYsj"
    val charge = stripe.Charge.create(Map(
      "amount" -> 100,
      "currency" -> "usd",
      "card" -> Map("number" -> "4242424242424242", "exp_month" -> 3, "exp_year" -> 2015)
    ))

See https://stripe.com/api for the most up-to-date documentation.
