package nl.dekkr.feedfrenzy.backend.model


object Action {}

case class Action(order: Int,
                  actionType: String,
                  inputVariable: Option[String],
                  outputVariable: Option[String],
                  template: Option[String],
                 // Only used by the Replace Action
                  replaceWith: Option[String],
                  // Only for DateParse action
                  locale: Option[String],
                  pattern: Option[String],
                  padTime: Option[Boolean]
                   )