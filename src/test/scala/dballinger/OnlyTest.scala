package dballinger

import dballinger.Only.{ManyElements, NoElement}
import org.scalatest.{FlatSpec, FunSuite, Matchers}

class OnlyTest extends FlatSpec with Matchers {

  "Only" should "return single element when only one exists" in {
    val expectedElement = 123
    val onlyOne = List(expectedElement).toIterable
    Only(onlyOne) should be(Right(expectedElement))
  }

  it should "return NoElement if the iterable is empty" in {
    val empty = Nil
    Only(empty) should be(Left(NoElement))
  }

  it should "return ManyElements if the iterable is empty" in {
    val multiple = List("a", "b")
    Only(multiple) should be(Left(ManyElements))
  }
}
