package nl.dekkr.feedfrenzy.backend.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter._

import nl.dekkr.feedfrenzy.backend.model.TypeOfAction.TypeOfAction
import nl.dekkr.feedfrenzy.backend.model._
import spray.json.DefaultJsonProtocol._
import spray.json._


object JsonFormatting extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport  {

  implicit val action = jsonFormat9(Action.apply)

  implicit val articleLinksRequest: RootJsonFormat[ArticleLinksRequest] = jsonFormat3(ArticleLinksRequest)
  implicit val articleRequest: RootJsonFormat[ArticleRequest] = jsonFormat3(ArticleRequest)
  implicit val articleListRequest: RootJsonFormat[ArticleListRequest] = jsonFormat4(ArticleListRequest)
  implicit val articleLinks: RootJsonFormat[ArticleLinks] = jsonFormat1(ArticleLinks)

  implicit val article: RootJsonFormat[Article] = jsonFormat7(Article)
  implicit val rawVariable: RootJsonFormat[RawVariable] = jsonFormat2(RawVariable)
  implicit val rawVariables: RootJsonFormat[RawVariables] = jsonFormat2(RawVariables)

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
