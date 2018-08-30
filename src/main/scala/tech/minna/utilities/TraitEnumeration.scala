package tech.minna.utilities

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.internal.Symbols

object TraitEnumeration {
  def values[A]: Set[A] = macro valuesImpl[A]

  def valuesImpl[A: c.WeakTypeTag](c: blackbox.Context) = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol.asClass

    if (!symbol.isClass || !symbol.isSealed)
      c.abort(c.enclosingPosition, "Can only enumerate values of a sealed trait or class.")
    else {

      val children = symbol.knownDirectSubclasses.toList

      if (!children.forall(_.isModuleClass)) {
        c.abort(c.enclosingPosition, "All children must be objects.")
      } else {
        val caseObjectSymbols = children.asInstanceOf[Seq[Symbols#Symbol]].map {
          _.sourceModule.asInstanceOf[Symbol]
        }

        q"""
           Set(..${caseObjectSymbols.map { symbol => q"""${symbol}""" }})
          """
      }
    }
  }
}
