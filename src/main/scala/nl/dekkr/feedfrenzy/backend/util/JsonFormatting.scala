package nl.dekkr.feedfrenzy.backend.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter._

import nl.dekkr.feedfrenzy.backend.model._
import spray.json.{DefaultJsonProtocol, _}

trait JsonFormatting extends DefaultJsonProtocol {
  implicit val action = jsonFormat9(Action.apply)
  implicit val articleLinksRequest = jsonFormat3(ArticleLinksRequest.apply)
  implicit val articleRequest = jsonFormat3(ArticleRequest.apply)
  implicit val articleLinks = jsonFormat1(ArticleLinks.apply)
  implicit val article = jsonFormat7(Article.apply)
  implicit val rawVariable = jsonFormat2(RawVariable.apply)
  implicit val rawVariables = jsonFormat2(RawVariables.apply)
  implicit val newContent = jsonFormat1(NewContent.apply)


  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {
    override def write(obj: OffsetDateTime) = JsString(obj.format(ISO_OFFSET_DATE_TIME))

    override def read(json: JsValue): OffsetDateTime = json match {
      case JsString(s) => OffsetDateTime.parse(s)
      case _ => throw new DeserializationException("Could not parse date/time")
    }
  }

  implicit object OptionOffsetDateTimeJsonFormat extends RootJsonFormat[Option[OffsetDateTime]] {
    override def write(obj: Option[OffsetDateTime]) = obj match {
      case None => JsNull
      case Some(dt) => JsString(dt.format(ISO_OFFSET_DATE_TIME))
    }

    override def read(json: JsValue): Option[OffsetDateTime] = json match {
      case JsString(s) => Some(OffsetDateTime.parse(s))
      case _ => throw new DeserializationException("Could not parse date/time")
    }
  }


}
