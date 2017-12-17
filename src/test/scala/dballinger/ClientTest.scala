package dballinger

import java.util.UUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import dballinger.Client.{NoAuthentication, Path}
import dballinger.models.{UnhappyResponse, UnparseableResponse}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class ClientTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  private val server = new WireMockServer(0)

  private def baseUrl = s"http://localhost:${server.port()}"

  "Client" should "parse response from unauthenticated post request" in {
    val path = s"/$aString"
    val requestBody = aJson
    val responseBody = aJson
    server.stubFor(
          post(urlEqualTo(path))
              .withDefaultHeaders
              .withRequestBody(equalToJson(requestBody.spaces2))
              .willReturn(aResponse().withBody(responseBody.spaces2))
        )

    val response = new Client(baseUrl).post(Path(path), requestBody, NoAuthentication)
    response should be(Right(responseBody))
  }

  it should "not be able to parse response from post request" in {
    val path = s"/$aString"
    val requestBody = aJson
    val responseBody = aString
    server.stubFor(
          post(urlEqualTo(path))
              .withDefaultHeaders
              .withRequestBody(equalToJson(requestBody.spaces2))
              .willReturn(aResponse().withBody(responseBody))
        )

    val response = new Client(baseUrl).post(Path(path), requestBody, NoAuthentication)
    response should be(Left(UnparseableResponse(responseBody)))
  }

  it should "a non 2xx response" in {
    val path = s"/$aString"
    val status = 400
    val requestBody = aJson
    val responseBody = aJson
    server.stubFor(
          post(urlEqualTo(path))
              .withDefaultHeaders
              .withRequestBody(equalToJson(requestBody.spaces2))
              .willReturn(aResponse()
                  .withStatus(status)
                  .withBody(responseBody.spaces2)
              )
        )

    val response = new Client(baseUrl).post(Path(path), requestBody, NoAuthentication)
    response should be(Left(UnhappyResponse(status, responseBody.spaces2)))
  }

  def aString: String = UUID.randomUUID().toString.split("-").head

  def aJson:Json = parse(s"""{"$aString":"$aString"}""") match {
    case Left(fail) => throw new Exception(fail.toString)
    case Right(json) => json
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    server.start()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  implicit class MappingBuilderOps(builder: MappingBuilder) {
    def withDefaultHeaders: MappingBuilder = withHeaders(
      ("Content-Type", "application/vnd.alertme.zoo-6.1+json"),
      ("Accept", "application/vnd.alertme.zoo-6.1+json"),
      ("X-Omnia-Client", "Hive Web Dashboard")
    )

    def withSessionHeader(sessionId: String): MappingBuilder = withHeaders(("X-Omnia-Access-Token", sessionId))

    def withHeaders(headers: (String, String)*): MappingBuilder = headers.foldLeft(builder)((builder, header) => builder.withHeader(header._1, equalTo(header._2)))
  }

}
