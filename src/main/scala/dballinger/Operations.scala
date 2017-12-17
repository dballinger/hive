package dballinger

import cats.syntax.either._
import dballinger.Client._
import dballinger.Only.OnlyFailure
import dballinger.models._
import dballinger.views.{NodesResponse, SessionRequest, SessionResponse}
import io.circe.DecodingFailure
import io.circe.generic.auto._
import io.circe.syntax._

class Operations(get:Get, post: Post) {

  import Operations._

  def login(username: Username, password: Password): Login = {
    val requestBody = SessionRequest(username, password).asJson
    for {
      json <- post(Path("auth/sessions"), requestBody, NoAuthentication)
      sessionResponse <- json.as[SessionResponse].leftMap(_ => UnparseableResponse(json.spaces2))
      session <- Only(sessionResponse.sessions).leftMap(InternalFailure)
    } yield SessionId(session.sessionId)
  }

  def listNodes(login: Login): NodeList = for {
    sessionId <- login
    json <- get(Path("nodes"), SessionAuthentication(sessionId))
    nodes <- json.as[NodesResponse].leftMap(_ => UnparseableResponse(json.spaces2))
  } yield nodes.nodes.map{
    n =>
      Node(NodeId(n.id), NodeName(n.name))
  }

  def findSingleNode(nodeList: NodeList): SingleNode = ???

  def setNode(login: Login, node: Node): SingleNode = ???
}

object Operations {
  type Login = Either[HiveFailure, SessionId]
  type NodeList = Either[HiveFailure, List[Node]]
  type SingleNode = Either[OnlyFailure, Node]
  type SetNode = Either[HiveFailure, Unit]

  private val client = Client()

  def apply() = new Operations(client.get, client.post)
}


