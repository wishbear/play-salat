package models

import org.specs2.mutable.{Around, Specification}
import play.api.test._
import play.api.test.Helpers._
import org.bson.types.ObjectId
import de.flapdoodle.embedmongo.{MongodProcess, MongoDBRuntime, MongodExecutable}
import de.flapdoodle.embedmongo.config.MongodConfig
import de.flapdoodle.embedmongo.distribution.Version
import org.specs2.execute.Result

class UserSpec extends Specification {

  val mongodExe: MongodExecutable = MongoDBRuntime.getDefaultInstance().prepare(new MongodConfig(Version.V2_0, 27018, false))
  val mongod: MongodProcess = mongodExe.start();

  def inMemoryMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "play-test-" + scala.util.Random.nextInt
    Map(
      ("mongodb." + name + ".db" -> dbname),
      ("mongodb." + name + ".port" -> 27018.toString)
    )
  }

  object emptyApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase())) {
        t // execute t inside a http session
      }
    }
  }

  object populatedApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase())) {
        User.save(User(
          username = "leon",
          password = "1234",
          address = Some(Address("Örebro", "123 45", "Sweden"))
        ))
        User.save(User(
          username = "guillaume",
          password = "1234",
    	  address = Some(Address("Paris", "75000", "France"))
        ))
        t // execute t inside a http session
      }
    }
  }

  "User instance" can {
    "be saved to mongo" in emptyApp {
      User.count() === 2 // 2 from global none from emptyApp
      val user: User = User(username="Jane Doe", password="password", address=Some(Address("Örebro", "123 45", "Sweden")))
      User.save(user)
      User.count() === 3
    }
    "be found in mongo" in populatedApp{
      User.count() === 4 //2 from global and 2 from populatedApp
      val Some(user) = User.findOneByUsername("guillaume")
      user.username === "guillaume"
    }
  }

  step(after())

  def after() = {
    mongod.stop()
    mongodExe.cleanup()
  }
}

