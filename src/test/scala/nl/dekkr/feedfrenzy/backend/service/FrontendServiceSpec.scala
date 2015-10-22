package nl.dekkr.feedfrenzy.backend.service

import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import nl.dekkr.feedfrenzy.backend.model._
import nl.dekkr.feedfrenzy.backend.services.FrontendService
import nl.dekkr.feedfrenzy.backend.test.TestHelper
import org.scalatest.{Matchers, WordSpec}

class FrontendServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with FrontendService with TestHelper {

  override val logger = Logging(system, getClass)

  val contentTypeHeader = RawHeader("Content-type", "application/json")

  val splitAction = Action(
    actionType = "split",
    order = 1,
    inputVariable = None,
    outputVariable = None,
    template = Some("div"),
    replaceWith = None,
    locale = None,
    pattern = None,
    padTime = None
  )


  "FrontendService" should {

//    "return a list of article links" in {
//      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(false))
//      Post("/v1/createArticleLinks", requestBody) ~> routes ~> check {
//        status shouldEqual OK
//        responseAs[ArticleLinks].urls.length should be > 0
//      }
//    }
//
//    "return a list of raw variable" in {
//      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(true))
//      Post("/v1/createArticleLinks", requestBody) ~> routes ~> check {
//        status shouldEqual OK
//        responseAs[RawVariables].variables.length shouldBe  0
//      }
//    }
//
//    "return a bad request on a empty / incorrect request" in {
//      val emptyUrlRequest = ArticleLinksRequest(url = "none", actions = List.empty[Action], raw = Some(false))
//      Post("/v1/createArticleLinks", emptyUrlRequest) ~> routes ~> check {
//        status shouldEqual BadRequest
//      }
//    }
//
//    "return an article" in {
//      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(false))
//      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
//        status shouldEqual OK
//        responseAs[Article] shouldEqual Article("http://google.com","",None,"",None,None,List())
//      }
//    }
//
//    "return the raw article variables" in {
//      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(true))
//      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
//        status shouldEqual OK
//        responseAs[RawVariables].variables.length shouldBe 1
//      }
//    }

    "leave GET requests to other paths unhandled" in {
      Get("/v1/nothing") ~> routes ~> check {
        handled shouldBe false
      }
    }

  }

}