package nl.dekkr.feedfrenzy.backend.model

import org.scalatest.{Matchers, WordSpec}

class PageUrlSpec extends WordSpec with Matchers {

  "PageUrl" should {

    "fail on a empty url " in {
      intercept[Exception] {
        PageUrl(url = "")
      }
    }

    "fail on a invalid protocol in the  url " in {
      intercept[Exception] {
        PageUrl(url = "file:///root.html")
      }
    }

    "fail on a invalid query in the  url " in {
      intercept[Exception] {
        PageUrl(url = "http://www.something.invalid/page.html?age=45&size= blue")
      }
    }

    "accept a valid url " in {
      PageUrl(url = "http://dekkr.nl/page.html?age=45&size=blue").isInstanceOf[PageUrl] shouldBe true
    }

  }

}
