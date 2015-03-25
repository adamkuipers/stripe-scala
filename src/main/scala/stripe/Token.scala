package stripe

case class Token(
  id: String,
  created: Int,
  livemode: Boolean,
  used: Boolean,
  card: Card) extends APIResource {
}

object Token extends APIResource {
  def create(params: Map[String,_]): Token = {
    request("POST", classURL, params).extract[Token]
  }

  def retrieve(id: String): Token = {
    request("GET", instanceURL(id)).extract[Token]
  }
}
