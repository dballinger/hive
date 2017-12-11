package dballinger

import java.util.UUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import dballinger.models.{Password, SessionId, Username}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class FunctionalTests extends FlatSpec with Matchers with BeforeAndAfterAll {

  private val server = new WireMockServer(0)

  private def baseUrl = s"http://localhost:${server.port()}"

  def ops = new Operations(baseUrl)

  def loginBody(sessionId: SessionId) =
    s"""{
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
       |}""".stripMargin

  "login" should "provide a session id" in {
    val username = Username(UUID.randomUUID().toString)
    val password = Password(UUID.randomUUID().toString)
    val sessionId = SessionId(UUID.randomUUID().toString)
    server.stubFor(
      post(urlEqualTo("/auth/sessions"))
          .withDefaultHeaders
          .withRequestBody(matchingJsonPath(s"$$.sessions[?(@.username == '${username.value}')]"))
          .withRequestBody(matchingJsonPath(s"$$.sessions[?(@.password == '${password.value}')]"))
          .withRequestBody(matchingJsonPath("$.sessions[?(@.caller == 'WEB')]"))
          .willReturn(aResponse().withBody(loginBody(sessionId)))
    )
    ops.login(username, password) should be(Right(sessionId))
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