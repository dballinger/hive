import dballinger.Operations
import dballinger.models.{Password, Username}

object Run extends App {
  val user = Username(System.getenv("HIVE_USER"))
  val pass = Password(System.getenv("HIVE_PASS"))

  println(Operations().login(user, pass))
}