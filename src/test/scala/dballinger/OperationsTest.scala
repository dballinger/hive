package dballinger

import dballinger.Client.{Authentication, NoAuthentication, Path, Post}
import io.circe.Json
import org.scalatest.{FlatSpec, Matchers}
import io.circe.parser._
import cats.syntax.either._
import Generators._
import dballinger.models.{Password, SessionId, Username}

class OperationsTest extends FlatSpec with Matchers {

  "Operations" should "login" in {
    val username = aUsername
    val password = aPassword
    val sessionId = aSessionId
    val expectedPath = Path("auth/sessions")
    val response = loginResponse(sessionId)
    val expectedRequest = loginRequest(username, password)
    val post: Post = stubPost(expectedPath, expectedRequest, NoAuthentication, response)

    val loginResult = new Operations(post).login(username, password)
    
    loginResult should be(Right(sessionId))
  }

  private def stubPost(expectedPath: Path, expectedRequest: Json, expectedAuth: Authentication, response: Json): Post = {
    (path, json, auth) => {
      if (path == expectedPath && json == expectedRequest && auth == expectedAuth)
        response.asRight
      else
        fail(s"Unexpected request: path=$path, auth=$auth, json=$json")
    }
  }

  def loginRequest(username: Username, password: Password): Json = unsafeParse(
    s"""
       |{
       |    "sessions": [{
       |        "username": "${username.value}",
       |        "password": "${password.value}",
       |        "caller": "WEB"
       |    }]
       |}
     """.stripMargin
  )

  def loginResponse(sessionId: SessionId): Json = unsafeParse(
    s"""
       |{
       |    "meta": {},
       |    "links": {},
       |    "linked": {},
       |    "sessions": [{
       |        "id": "${sessionId.value}",
       |        "links": {},
       |        "username": "joe.bloggs@email.com",
       |        "userId": "cf82b9d8-8d0b-43b7-ae28-xxxxxxxxxxxx",
       |        "extCustomerLevel": 1,
       |        "latestSupportedApiVersion": "6",
       |        "sessionId": "${sessionId.value}"
       |    }]
       |}
     """.stripMargin
  )

  private def unsafeParse(str: String): Json = parse(str) match {
    case Right(json) => json
    case Left(f) => throw new Exception(f)
  }
}
