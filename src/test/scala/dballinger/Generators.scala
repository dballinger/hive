package dballinger

import java.util.UUID

import dballinger.models.{Password, SessionId, Username}
import io.circe.Json
import io.circe.parser.parse

object Generators {
  def aString: String = UUID.randomUUID().toString.split("-").head

  def aJson: Json = parse(s"""{"$aString":"$aString"}""") match {
    case Left(fail) => throw new Exception(fail.toString)
    case Right(json) => json
  }

  def aUsername = Username(aString)

  def aPassword = Password(aString)

  def aSessionId = SessionId(aString)
}