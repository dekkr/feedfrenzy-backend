package nl.dekkr.feedfrenzy.backend.model




  trait ActionType {
  }

  case class CssSelector(inputVariable: Option[String],outputVariable: Option[String],selectorPattern : String) extends ActionType
  case class CssSelectorRemove(inputVariable: Option[String],outputVariable: Option[String],selectorPattern : String) extends ActionType
  case class CssSelectorParent(inputVariable: Option[String],outputVariable: Option[String],selectorPattern : String) extends ActionType
  case class Attribute(inputVariable: Option[String],outputVariable: Option[String],attribute : String) extends ActionType
  case class Regex(inputVariable: Option[String],outputVariable: Option[String],regex : String) extends ActionType
  case class Template(outputVariable: Option[String],template : String) extends ActionType
  case class Replace(inputVariable: Option[String],outputVariable: Option[String],find : String,replaceWith: String) extends ActionType
  case class Split(inputVariable: Option[String],outputVariable: Option[String],selectorPattern : String) extends ActionType

object Action {
}




case class Action(order: Int,
                  actionType : String,
                  inputVariable: Option[String],
                  outputVariable: Option[String],
                  template : Option[String],
                  replaceWith: Option[String])

case class ArticleLinksRequest(url: String, indexActions:List[Action])

case class ArticleLinks(urls: List[String])
