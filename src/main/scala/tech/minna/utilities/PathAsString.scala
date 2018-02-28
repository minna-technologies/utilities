package tech.minna.utilities

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

// Inspired by https://github.com/pathikrit/sauron/blob/master/src/main/scala/com/github/pathikrit/sauron/package.scala

object PathAsString {
  /**
    * Converts a field path to a string of the path.
    *
    * @return The path as a string.
    */
  def pathAsString[A, B](path: A => B): String = macro pathAsStringImpl[A, B]

  def pathAsStringImpl[A, B](c: blackbox.Context)(path: c.Expr[A => B]): c.Tree = {
    import c.universe._

    /**
      * @example (_.p.q.r) -> List(p, q, r)
      */
    def split(accessor: c.Tree): List[c.TermName] = accessor match {
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
