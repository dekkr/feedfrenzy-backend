package nl.dekkr.feedfrenzy.backend.model


object Action {}

case class Action(order: Int,
                  actionType: String,
                  inputVariable: Option[String],
                  outputVariable: Option[String],
                  template: Option[String],
                  replaceWith: Option[String])