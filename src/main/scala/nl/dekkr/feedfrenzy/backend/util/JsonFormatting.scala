package nl.dekkr.feedfrenzy.backend.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter._

import nl.dekkr.feedfrenzy.backend.model.TypeOfAction.TypeOfAction
import nl.dekkr.feedfrenzy.backend.model._
import spray.json.DefaultJsonProtocol._
import spray.json._


trait JsonFormatting extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport {
  implicit val action = jsonFormat9(Action.apply)
  implicit val articleLinksRequest = jsonFormat3(ArticleLinksRequest.apply)
  implicit val articleRequest = jsonFormat3(ArticleRequest.apply)
  implicit val articleLinks = jsonFormat1(ArticleLinks.apply)
  implicit val article = jsonFormat7(Article.apply)
  implicit val rawVariable = jsonFormat2(RawVariable.apply)
  implicit val rawVariables = jsonFormat2(RawVariables.apply)

  implicit object TypeOfActionJsonFormat extends RootJsonFormat[TypeOfAction] {
    override def write(toa: TypeOfAction) = JsString(TypeOfAction.typeOfAction2String(toa))

    override def read(json: JsValue): TypeOfAction = json match {
      case JsString(s) => TypeOfAction.string2typeOfAction(s)
      case _ => throw new DeserializationException("Invalid action in Json")
    }
  }

  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {
    override def write(obj: OffsetDateTime) = JsString(obj.format(ISO_OFFSET_DATE_TIME))

    override def read(json: JsValue): OffsetDateTime = json match {
      case JsString(s) => OffsetDateTime.parse(s)
      case _ => throw new DeserializationException("Could not parse date/time")
    }
  }

}
