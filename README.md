# Minna Utilities

This library provides various helpful utilities for when working with Scala code.

## Setup

Add the following to your `build.sbt` file:
```scala
libraryDependencies += "tech.minna" %% "utilities" % "1.2.0"
resolvers += Resolver.bintrayRepo("minna-technologies", "maven")
```

This library is compiled for Scala 2.12.

## Documentation

### PathAsString.pathAsString

Converts a field path to a string.

```scala
PathAsString.pathAsString[Family](_.mother.name.last) => "mother.name.last"
PathAsString.pathAsString((f: Family) => f.mother.name.last) => "mother.name.last"

// Options and collections can be unlifted by ~>
PathAsString.pathAsString[Family](_.father.~>.name.first) => "father.name.first"
PathAsString.pathAsString[Family](_.father.map(_.name).first) => "father.name.first"
```
