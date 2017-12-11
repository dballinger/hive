package dballinger.views

import dballinger.models.{Password, Username}

case class SessionRequest(sessions: List[SessionRequestInner])

case class SessionRequestInner(username: String, password: String, caller: String)

object SessionRequest {
  def apply(username: Username, password: Password) = new SessionRequest(List(SessionRequestInner(username.value, password.value, "WEB")))
}


case class SessionResponse(sessions: List[SessionResponseInner])

case class SessionResponseInner(sessionId: String)

