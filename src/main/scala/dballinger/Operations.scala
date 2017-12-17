package dballinger

import cats.syntax.either._
import dballinger.Client.{NoAuthentication, Path, Post}
import dballinger.Only.OnlyFailure
import dballinger.models._
import dballinger.views.{SessionRequest, SessionResponse}
import io.circe.generic.auto._
import io.circe.syntax._

class Operations(post: Post) {

  type Login = Either[AnyRef, SessionId]
  type NodeList = Either[HiveFailure, List[Node]]
  type SingleNode = Either[OnlyFailure, Node]
  type SetNode = Either[HiveFailure, Unit]

  def login(username: Username, password: Password): Login = {
    val requestBody = SessionRequest(username, password).asJson
    for {
      json <- post(Path("auth/sessions"), requestBody, NoAuthentication)
      sessionResponse <- json.as[SessionResponse]
      session <- Only(sessionResponse.sessions)
    } yield SessionId(session.sessionId)
  }

  def listNodes(login: Login): NodeList = ???

  def findSingleNode(nodeList: NodeList): SingleNode = ???

  def setNode(login: Login, node: Node): SingleNode = ???
}

object Operations {
  private val client = Client()
  def apply() = new Operations(client.post)
}


