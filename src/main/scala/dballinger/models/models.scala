package dballinger.models

case class Username(value: String)

case class Password(value: String)

case class SessionId(value: String) {
  def header = ("X-Omnia-Access-Token", value)
}

case class Node(id:String, name:String)

sealed trait HiveFailure

trait HttpFailure extends HiveFailure

case class UnhappyResponse(status: Int, body: String) extends HttpFailure
case class UnparseableResponse(body: String) extends HttpFailure