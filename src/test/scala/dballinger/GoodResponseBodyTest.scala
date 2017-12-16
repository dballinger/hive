package dballinger

import java.util.UUID

import dballinger.models.UnhappyResponse
import org.scalatest.{FlatSpec, Matchers}

import scalaj.http.HttpResponse

class GoodResponseBodyTest extends FlatSpec with Matchers {
  "GoodResponseBody" should "return the body for the 2xx range" in {
    val expectedBody = UUID.randomUUID().toString
    val _200 = HttpResponse(expectedBody, 200, Map())
    val _299 = HttpResponse(expectedBody, 299, Map())
    GoodResponseBody(_200) should be(Right(expectedBody))
    GoodResponseBody(_299) should be(Right(expectedBody))
  }

  it should "return the UnhappyResponse for anything outside the 2xx range" in {
    val expectedBody = UUID.randomUUID().toString
    val _199 = HttpResponse(expectedBody, 199, Map())
    val _300 = HttpResponse(expectedBody, 300, Map())
    GoodResponseBody(_199) should be(Left(UnhappyResponse(199, expectedBody)))
    GoodResponseBody(_300) should be(Left(UnhappyResponse(300, expectedBody)))
  }
}
