package tech.minna.utilities

import org.scalatest.{FlatSpec, Matchers}
import tech.minna.utilities.PathAsString._

case class Relatives(
  closest: Option[Family]
)

case class Family(
  children: Seq[Person],
  father: Option[Person],
  mother: Person
)

case class Person(
  name: PersonName
)

case class PersonName(
  first: String,
  last: String
) {
  def middle(): String = ???

  def given: Option[String] = ???

  def multipleLast: Seq[String] = ???
}

class PathAsStringSpec extends FlatSpec with Matchers {
  "PathAsString.pathAsString" should "convert a path to a string" in {
    pathAsString((p: Person) => p) shouldEqual ""
    pathAsString((p: Person) => p.name) shouldEqual "name"
    pathAsString((p: Person) => p.name.first) shouldEqual "name.first"
    pathAsString[Person](_.name.first) shouldEqual "name.first"
    pathAsString((f: Family) => f.mother.name.last) shouldEqual "mother.name.last"
  }

  it should "convert a path with map or flatMap to a string" in {
    pathAsString[Family](_.father.map(_.name)) shouldEqual "father.name"
    pathAsString[Family](_.father.flatMap(_.name.given)) shouldEqual "father.name.given"
    pathAsString[Family](_.children.map(_.name.first)) shouldEqual "children.name.first"
    pathAsString[Family](_.children.flatMap(_.name.multipleLast)) shouldEqual "children.name.multipleLast"
    pathAsString[Relatives](_.closest.flatMap(_.father).map(_.name)) shouldEqual "closest.father.name"
    pathAsString[Relatives](_.closest.flatMap(_.father.map(_.name))) shouldEqual "closest.father.name"
  }

  it should "not compile for method calls" in {
    "pathAsString((f: Family) => f.mother.name.middle())" shouldNot compile
    "pathAsString((f: Family) => f.children(1).name)" shouldNot compile
  }

  it should "not compile for non paths" in {
    "pathAsString((f: Family) => identity(f.mother).name" shouldNot compile
    "pathAsString((f: Family) => 123" shouldNot compile
    "pathAsString((f: Family) => f.mother.name + \"?\")" shouldNot compile
    "pathAsString((f: Family) => \"Hey \" + f.father.name)" shouldNot compile
  }

  "PathAsString.pathOfTypeAsString" should "convert a path to a string" in {
    pathOfTypeAsString[Person, PersonName](_.name) shouldEqual "name"
    pathOfTypeAsString[Person, String](_.name.first) shouldEqual "name.first"
  }
}
