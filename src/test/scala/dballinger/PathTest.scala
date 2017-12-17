package dballinger

import java.util.UUID

import dballinger.Client.Path
import org.scalatest.{FlatSpec, Matchers}

class PathTest extends FlatSpec with Matchers {

  "Path" should "not add a slash if it is already included" in {
    val pathStr = s"/$aString"
    Path(pathStr).toString should be(pathStr)
  }

  it should "add a slash if it is not already included" in {
    val pathStr = aString
    Path(pathStr).toString should be(s"/$pathStr")
  }

  def aString: String = UUID.randomUUID().toString.split("-").head
}
