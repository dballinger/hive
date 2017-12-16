package dballinger

import cats.Eval
import dballinger.models._
import dballinger.views.{SessionRequest, SessionResponse}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import cats.syntax.either._
import Operations._

import scalaj.http.{Http, HttpRequest, HttpResponse}

class Operations(baseUrl: String) {

  def http(path: String): HttpRequest = Http(s"$baseUrl/$path").headers(
    ("Content-Type", "application/vnd.alertme.zoo-6.1+json"),
    ("Accept", "application/vnd.alertme.zoo-6.1+json"),
    ("X-Omnia-Client", "Hive Web Dashboard")
  )

  type Login = Either[AnyRef, SessionId]
  type NodeList = Either[HiveFailure, String]
  type SingleNode = Either[HiveFailure, String]

  def login(username: Username, password: Password): Login = {
    val requestBody = SessionRequest(username, password).asJson.noSpaces
    val response: HttpResponse[String] = http("auth/sessions").postData(requestBody).asString
    for {
      body <- GoodResponseBody(response)
      json <- parse(response.body)
      sessionResponse <- json.as[SessionResponse]
      session <- Only(sessionResponse.sessions)
    } yield SessionId(session.sessionId)
  }

  def listNodes(login: Login): NodeList = ???

  def findSingleNode(nodeList: NodeList): SingleNode = ???

  def setNode(login: Login, node: Node): SingleNode = ???
}

object Operations {
  def apply() = new Operations("https://api-prod.bgchprod.info:443/omnia")
}


