package dballinger

import dballinger.Client.{Authentication, Get, Path, Post}
import dballinger.models.{HttpFailure, SessionId, UnparseableResponse}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import cats.syntax.either._

import scalaj.http.{Http, HttpRequest}

class Client(baseUrl: String) {
  val post: Post = {
    (path, requestBody, authentication) =>
      call(path, authentication, _.postData(requestBody.noSpaces))
  }

  val get: Get = {
    (path, authentication) =>
      call(path, authentication, identity)
  }

  def call(path: Path, authentication: Authentication, fn: HttpRequest => HttpRequest): Either[HttpFailure, Json] = {
    val response = fn(http(path, authentication)).asString
    for {
      body <- GoodResponseBody(response)
      parsed <- parse(body).leftMap(_ => UnparseableResponse(response.body))
    } yield parsed
  }

  private def http(path: Path, authentication: Authentication): HttpRequest = authentication(
    Http(s"$baseUrl$path").headers(
      ("Content-Type", "application/vnd.alertme.zoo-6.1+json"),
      ("Accept", "application/vnd.alertme.zoo-6.1+json"),
      ("X-Omnia-Client", "Hive Web Dashboard")
    )
  )
}

object Client {

  def apply() = new Client("https://api-prod.bgchprod.info:443/omnia")

  type Post = (Path, Json, Authentication) => Either[HttpFailure, Json]
  type Get = (Path, Authentication) => Either[HttpFailure, Json]

  class Path(val value: String) {
    override def toString: String = value

    def canEqual(other: Any): Boolean = other.isInstanceOf[Path]

    override def equals(other: Any): Boolean = other match {
      case that: Path =>
        (that canEqual this) &&
            value == that.value
      case _ => false
    }

    override def hashCode(): Int = {
      val state = Seq(value)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
  }

  object Path {
    def apply(str: String): Path = if (str.startsWith("/")) new Path(str) else new Path(s"/$str")
  }

  sealed trait Authentication {
    def apply(req: HttpRequest): HttpRequest = this match {
      case SessionAuthentication(SessionId(sessionStr)) => req.header("X-Omnia-Access-Token", sessionStr)
      case _ => req
    }
  }

  case object NoAuthentication extends Authentication

  case class SessionAuthentication(sessionId: SessionId) extends Authentication

}