package tech.minna.utilities

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

// Inspired by https://github.com/pathikrit/sauron/blob/master/src/main/scala/com/github/pathikrit/sauron/package.scala

object PathAsString {

  // @compileTimeOnly("iterable.~> is only intended to be used in combination with PathAsString macro")
  implicit class PathTraverseTraversableOnce[T](iterable: IterableOnce[T]) {
    def ~> : T = ???
  }

  /**
    * Converts a field path to a string of the path.
    *
    * Ignores `map` and `flatMap` as terms if they are part of the path.
    *
    * @example _.p.q.r => "p.q.r"
    * @example _.p.~>.q.r => "p.q.r"
    * @example _.p.map(_.q).r => "p.q.r"
    * @example _.p.flatMap(_.q).r => "p.q.r"
    * @return The path as a string.
    */
  def pathAsString[A](path: A => Any): String = macro pathAsStringImpl[A, Any]

  /**
    * Converts a field path of a certain type to a string of the path.
    *
    * @return The path as a string.
    */
  def pathOfTypeAsString[A, B](path: A => B): String = macro pathAsStringImpl[A, B]

  def pathAsStringImpl[A, B](c: blackbox.Context)(path: c.Expr[A => B]): c.Tree = {
    import c.universe._

    /**
      * @example (_.p.q.r) -> List(p, q, r)
      * @example (_.p.~>.q.r) -> List(p, q, r)
      * @example (_.p.map(_.q).r) -> List(p, q, r)
      */
    def split(accessor: c.Tree): scala.List[c.TermName] = accessor match {
      case q"$pq.~>" if pq.tpe.typeConstructor.=:=(weakTypeOf[PathTraverseTraversableOnce[_]].typeConstructor) =>
        val q"$_($r)" = pq
        split(r)
      case q"$pq.map[..$_](($_) => $nestedAccessor)" => split(pq) ++ split(nestedAccessor)
      case q"$pq.map[..$_](($_) => $nestedAccessor)(..$_)" => split(pq) ++ split(nestedAccessor)
      case q"$pq.flatMap[..$_](($_) => $nestedAccessor)" => split(pq) ++ split(nestedAccessor)
      case q"$pq.flatMap[..$_](($_) => $nestedAccessor)(..$_)" => split(pq) ++ split(nestedAccessor)
      case q"$pq.$r" => split(pq) :+ r
      case _: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $accessor")
    }

    path.tree match {
      case q"($_) => $accessor" =>
        val pathAsString = split(accessor)
          .map(_.decodedName.toString)
          .mkString(".")

        Literal(Constant(pathAsString))
      case _ =>
        c.abort(c.enclosingPosition, s"Path must have shape: _.a.b.c.(...); got: ${path.tree}")
    }
  }
}
