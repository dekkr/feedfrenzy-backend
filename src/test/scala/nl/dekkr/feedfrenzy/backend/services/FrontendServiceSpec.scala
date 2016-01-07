package nl.dekkr.feedfrenzy.backend.services

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import nl.dekkr.feedfrenzy.backend.model._
import nl.dekkr.feedfrenzy.backend.test.{MockPageFetcher, TestHelper}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.duration._
import scala.language.postfixOps

class FrontendServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with FrontendService with TestHelper with BeforeAndAfter {

  override val logger = Logging(system, getClass)

  implicit val routeTestTimeout = RouteTestTimeout(5.second)

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

  val pf = new MockPageFetcher

  override def beforeAll() {
    pf.startApi()
  }

  override def afterAll() {
    pf.stopApi()
  }


  Http().bindAndHandle(routes, interface = API_INTERFACE, port = API_PORT)

  "FrontendService" should {

    "return a list of article links" in {
      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(false))
      Post("/v1/createArticleLinks", requestBody) ~> routes ~> check {
        status shouldEqual OK
        responseAs[ArticleLinks].urls.length should be > 0
      }
    }

    "return a list of raw variable" in {
      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(true))
      Post("/v1/createArticleLinks", requestBody) ~> routes ~> check {
        status shouldEqual OK
        responseAs[RawVariables].variables.length shouldBe 0
      }
    }

    "return a bad request on a empty / incorrect request" in {
      val emptyUrlRequest = ArticleLinksRequest(url = "none", actions = List.empty[Action], raw = Some(false))
      Post("/v1/createArticleLinks", emptyUrlRequest) ~> routes ~> check {
        status shouldEqual BadRequest
      }
    }

    "return an article" in {
      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(false))
      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
        status shouldEqual OK
        responseAs[Article] shouldEqual Article("http://google.com", "", None, "", None, None, List())
      }
    }

    "return the raw article variables" in {
      val requestBody = ArticleLinksRequest(url = "http://google.com", actions = List(splitAction), raw = Some(true))
      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
        status shouldEqual OK
        responseAs[RawVariables].variables.length shouldBe 1
      }
    }

    "return an bad request when the url is invalid" in {
      val requestBody = ArticleLinksRequest(url = "httpx://google.com", actions = List(splitAction), raw = Some(false))
      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
        status shouldEqual BadRequest
        responseAs[String] shouldEqual "httpx://google.com: incorrect url"
      }
    }

    "return an internal server error on non-existing host" in {
      val requestBody = ArticleLinksRequest(url = "http://notfound.dekkr.nl", actions = List(splitAction), raw = Some(false))
      Post("/v1/createArticle", requestBody) ~> addHeader(contentTypeHeader) ~> routes ~> check {
        status shouldBe InternalServerError
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/v1/nothing") ~> routes ~> check {
        handled shouldBe false
      }
    }

  }


}