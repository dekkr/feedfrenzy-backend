package nl.dekkr.feedfrenzy.backend.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import nl.dekkr.feedfrenzy.backend.model.{NewContent, PageUrl}
import nl.dekkr.feedfrenzy.backend.test.MockPageFetcher
import org.scalatest.{Matchers, WordSpec}


class BackendServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with Configuration {


  override val PAGEFETCHER_PORT = 9091
  val pf = new MockPageFetcher(PAGEFETCHER_PORT)

  val backend = new BackendService(pf.url)

  "BackendService" should {
    "retrieve an article" in {
      val article = backend.getArticle(PageUrl(url = "http://google.com"))
      article match {
        case x: NewContent => //okay
        case _ => fail("Incorrect response")
      }
    }

    "retrieve a set of articles" in {
      val articles = backend.getArticles(PageUrl(url = "http://google.com"))
      articles match {
        case x: NewContent => //okay
        case _ => fail("Incorrect response")
      }
    }
  }
}
