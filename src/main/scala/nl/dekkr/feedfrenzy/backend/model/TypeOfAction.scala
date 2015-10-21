package nl.dekkr.feedfrenzy.backend.model

import scala.language.implicitConversions

object TypeOfAction extends Enumeration {
  type TypeOfAction = Value
  val Attribute = Value("attribute")
  val CssSelector = Value("css-selector")
  val CssSelectorParent = Value("css-parent")
  val CssSelectorRemove = Value("css-remove")
  val DateParser = Value("date-parser")
  val Regex = Value("regex")
  val Replace = Value("replace")
  val Split = Value("split")
  val Template = Value("template")
  val NotImplemented = Value("not-implemented")

  implicit def string2typeOfAction(str: String): TypeOfAction = {
    try {
      TypeOfAction.withName(str)
    } catch {
      case e: Exception => TypeOfAction.NotImplemented
    }
  }

  implicit def typeOfAction2String(toa: TypeOfAction): String = toa.toString

}
