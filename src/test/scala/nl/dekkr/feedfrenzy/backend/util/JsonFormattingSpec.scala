package nl.dekkr.feedfrenzy.backend.util

import java.time.OffsetDateTime

import org.scalatest.{Matchers, WordSpec}
import spray.json._


class JsonFormattingSpec  extends WordSpec with Matchers with JsonFormatting {

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
