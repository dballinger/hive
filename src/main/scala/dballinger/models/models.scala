package dballinger.models

case class Username(value: String)

case class Password(value: String)

case class SessionId(value: String) {
  def header = ("X-Omnia-Access-Token", value)
}

case class Node(id: NodeId, name: NodeName)

case class NodeId(value: String)

case class NodeName(value: String)

sealed trait HiveFailure

case class InternalFailure(cause: AnyRef) extends HiveFailure

trait HttpFailure extends HiveFailure

case class UnhappyResponse(status: Int, body: String) extends HttpFailure

case class UnparseableResponse(body: String) extends HttpFailure