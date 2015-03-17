package com.stripe

case class Customer(
  created: Long,
  id: String,
  livemode: Boolean,
  description: Option[String],
  cards: CardCollection,
  defaultCard: Option[String],
  email: Option[String],
  delinquent: Option[Boolean],
  subscription: Option[Subscription],
  discount: Option[Discount],
  accountBalance: Option[Int]) extends APIResource {
  def update(params: Map[String,_]): Customer = {
    request("POST", instanceURL(this.id), params).extract[Customer]
  }

  def delete(): DeletedCustomer = {
    request("DELETE", instanceURL(this.id)).extract[DeletedCustomer]
  }

  def updateSubscription(params: Map[String,_]): Subscription = {
    request("POST", "%s/subscription".format(instanceURL(id)), params).extract[Subscription]
  }

  def cancelSubscription(params: Map[String,_] = Map.empty): Subscription = {
    request("DELETE", "%s/subscription".format(instanceURL(id)), params).extract[Subscription]
  }
}

case class DeletedCustomer(id: String, deleted: Boolean)

case class CustomerCollection(count: Int, data: List[Customer])

object Customer extends APIResource {
  def create(params: Map[String,_]): Customer = {
    request("POST", classURL, params).extract[Customer]
  }

  def retrieve(id: String): Customer = {
    request("GET", instanceURL(id)).extract[Customer]
  }

  def all(params: Map[String,_] = Map.empty): CustomerCollection = {
    request("GET", classURL, params).extract[CustomerCollection]
  }
}
