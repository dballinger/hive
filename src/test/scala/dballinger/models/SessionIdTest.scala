package dballinger.models

import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class SessionIdTest extends FlatSpec with Matchers {
  "SessionId" should "provide the header details that should be passed to the server" in {
    val expectedHeaderName = "X-Omnia-Access-Token"
    val expectedHeaderValue = UUID.randomUUID().toString
    SessionId(expectedHeaderValue).header should be((expectedHeaderName, expectedHeaderValue))
  }
}
