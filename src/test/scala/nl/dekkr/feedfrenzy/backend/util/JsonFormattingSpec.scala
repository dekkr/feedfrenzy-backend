package nl.dekkr.feedfrenzy.backend.util

import org.scalatest.{Matchers, WordSpec}


class JsonFormattingSpec extends WordSpec with Matchers {

  import nl.dekkr.feedfrenzy.backend.util.JsonFormatting._
  import spray.json._

  "JsonFormatting" should {
    "format OffsetDateTime" in {
      val dateString = "2016-01-06T22:19:05.048+01:00"
      OffsetDateTimeJsonFormat.write(OffsetDateTimeJsonFormat.read(JsString(dateString))) shouldBe JsString(dateString)
    }

    "fail on invalid Json type" in {
      intercept[DeserializationException] {
        OffsetDateTimeJsonFormat.read(JsBoolean(false))
      }
    }

    "fail on invalid TypeOfAction" in {
      intercept[DeserializationException] {
        TypeOfActionJsonFormat.read(JsBoolean(false))
      }
    }


  }


}
