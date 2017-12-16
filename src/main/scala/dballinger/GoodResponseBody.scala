package dballinger

import scalaj.http.HttpResponse
import cats.syntax.either._
import dballinger.models.UnhappyResponse

object GoodResponseBody {
  def apply(response: HttpResponse[String]): Either[UnhappyResponse, String] =
    if (response.code >= 200 && response.code <= 299) response.body.asRight
    else UnhappyResponse(response.code, response.body).asLeft

}