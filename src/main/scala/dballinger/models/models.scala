package dballinger.models

case class Username(value: String)

case class Password(value: String)

case class SessionId(value: String) {
  def header = ("X-Omnia-Access-Token", value)
}

case class Node()

sealed trait HiveFailure

case class UnhappyResponse(status: Int, body: String) extends HiveFailure