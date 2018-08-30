package tech.minna.utilities

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.internal.Symbols

object TraitEnumeration {
  /**
    * Generate enumeration values from all the case objects inherited from a sealed trait. The sealed trait
    * can only be inherited by case objects.
    *
    * @example
    * {{{
    * object Vehicle {
    *   case object Bus extends Vehicle
    *   case object Car extends Vehicle
    *   case object MiniVan extends Vehicle
    *
    *   val all = TraitEnumeration.values[Vehicle]
    * }
    * }}}
    *
    * @return A set of the case objects inheriting the given sealed trait.
    */
  def values[A]: Set[A] = macro valuesImpl[A]

  def valuesImpl[A: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol.asClass

    if (!symbol.isClass || !symbol.isSealed) {
      c.abort(c.enclosingPosition, "Can only generate enumeration values of a sealed trait.")
    } else {
      val children = symbol.knownDirectSubclasses.toList

      if (!children.forall(_.isModuleClass)) {
        c.abort(c.enclosingPosition, "Can only generate enumeration values for case objects which extend the sealed trait.")
      } else {
        val caseObjectSymbols = children.asInstanceOf[Seq[Symbols#Symbol]].map {
          _.sourceModule.asInstanceOf[Symbol]
        }

        q"""
           Set(..${caseObjectSymbols.map { symbol => q"$symbol" }})
          """
      }
    }
  }
}
