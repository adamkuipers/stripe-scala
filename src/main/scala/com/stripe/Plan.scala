package com.stripe

case class Plan(
  id: String,
  name: String,
  interval: String,
  amount: Int,
  currency: String,
  livemode: Boolean,
  trialPeriodDays: Option[Int]) extends APIResource {
  def update(params: Map[String,_]): Plan = {
    request("POST", instanceURL(this.id), params).extract[Plan]
  }

  def delete(): DeletedPlan = {
    request("DELETE", instanceURL(this.id)).extract[DeletedPlan]
  }
}

case class PlanCollection(count: Int, data: List[Plan])

case class DeletedPlan(id: String, deleted: Boolean)

object Plan extends APIResource {
  def create(params: Map[String,_]): Plan = {
    request("POST", classURL, params).extract[Plan]
  }

  def retrieve(id: String): Plan = {
    request("GET", instanceURL(id)).extract[Plan]
  }

  def all(params: Map[String,_] = Map.empty): PlanCollection = {
    request("GET", classURL, params).extract[PlanCollection]
  }
}
