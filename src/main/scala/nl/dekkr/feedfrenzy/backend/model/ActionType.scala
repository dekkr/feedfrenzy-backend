package nl.dekkr.feedfrenzy.backend.model

sealed trait ActionType { }


  case class CssSelector(inputVariable: Option[String], outputVariable: Option[String], selectorPattern: String) extends ActionType

  case class CssSelectorRemove(inputVariable: Option[String], outputVariable: Option[String], selectorPattern: String) extends ActionType

  case class CssSelectorParent(inputVariable: Option[String], outputVariable: Option[String], selectorPattern: String) extends ActionType

  case class Attribute(inputVariable: Option[String], outputVariable: Option[String], attribute: String) extends ActionType

  case class Regex(inputVariable: Option[String], outputVariable: Option[String], regex: String) extends ActionType

  case class DateRegex(inputVariable: Option[String], outputVariable: Option[String], regex: String) extends ActionType

  case class Template(outputVariable: Option[String], template: String) extends ActionType

  case class Replace(inputVariable: Option[String], outputVariable: Option[String], find: String, replaceWith: String) extends ActionType

  case class Split(inputVariable: Option[String], outputVariable: Option[String], selectorPattern: String) extends ActionType


