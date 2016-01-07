package nl.dekkr.feedfrenzy.backend.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import nl.dekkr.feedfrenzy.backend.model.{NewContent, PageUrl}
import nl.dekkr.feedfrenzy.backend.test.MockPageFetcher
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}


class BackendServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {


  val pf = new MockPageFetcher

  override def beforeAll() {
    pf.startApi()
  }

  override def afterAll() {
    pf.stopApi()
  }


  val backend = new BackendService(pf.url)

  "Backend service" should {
    "retrieve an article" in {
      val article = backend.getArticle(PageUrl(url = "http://google.com"))
      article match {
        case x: NewContent => //okay
        case _ => fail("Incorrect response")
      }
    }

//    "fail retrieving an article for an incorrect url" in {
//      val article = backend.getArticle(PageUrl(url = "http://notfound.dekkr.nl"))
//      article match {
//        case x: NewContent => fail(s"Incorrect response: ${x.body}")
//        case _ =>
//      }
//    }


    "retrieve a set of articles" in {
      val articles = backend.getArticles(PageUrl(url = "http://google.com"))
      articles match {
        case x: NewContent => //okay
        case _ => fail("Incorrect response")
      }
    }
  }
}
