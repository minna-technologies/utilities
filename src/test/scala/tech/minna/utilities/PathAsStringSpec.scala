package tech.minna.utilities

import org.scalatest.{FlatSpec, Matchers}
import tech.minna.utilities.PathAsString.pathAsString

case class Family(
  children: Seq[Person],
  father: Person,
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
}

class PathAsStringSpec extends FlatSpec with Matchers {
  "PathAsString" should "convert a path to a string" in {
    pathAsString((p: Person) => p) shouldEqual ""
    pathAsString((p: Person) => p.name) shouldEqual "name"
    pathAsString((p: Person) => p.name.first) shouldEqual "name.first"
    pathAsString((f: Family) => f.mother.name.last) shouldEqual "mother.name.last"
  }

  it should "not compile for method calls" in {
    "pathAsString((f: Family) => f.mother.name.middle())"  shouldNot compile
    "pathAsString((f: Family) => f.children(1).name)"  shouldNot compile
  }

  it should "not compile for non paths" in {
    "pathAsString((f: Family) => identity(f.mother).name"  shouldNot compile
    "pathAsString((f: Family) => 123"  shouldNot compile
    "pathAsString((f: Family) => f.mother.name + \"?\")"  shouldNot compile
    "pathAsString((f: Family) => \"Hey \" + f.father.name + \"?\")"  shouldNot compile
  }
}
