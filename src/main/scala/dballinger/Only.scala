package dballinger

import cats.syntax.either._

object Only {
  def apply[T](iterable: Iterable[T]): Either[OnlyFailure, T] = iterable match {
    case x :: Nil => x.asRight
    case Nil => NoElement.asLeft
    case _ => ManyElements.asLeft
  }

  sealed trait OnlyFailure

  case object NoElement extends OnlyFailure

  case object ManyElements extends OnlyFailure

}