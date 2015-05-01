package nl.dekkr.feedfrenzy.backend
import org.specs2.mutable.Specification


class FrontendServiceSpec extends Specification {
  //with FrontendService {

  /*
  def actorRefFactory = system

  "FrontendService" should {

    "show a default index page" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Ready to serve")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/do_not_respond") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "fail on a non-existing host " in {
      Get("/v1/page?url=http://not.there.dekkr.nl") ~> myRoute ~> check {
        status === NotFound
        responseAs[String] must contain("Host not found")
      }
    }

    "fail on a missing url " in {
      Get("/v1/page") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "successfully fetch a page " in {
      Get("/v1/page?url=https://google.com") ~> myRoute ~> check {
        responseAs[String] must contain("google")
      }
    }

    "successfully fetch a page a second time, hitting the cache" in {
      Get("/v1/page?url=https://google.com") ~> myRoute ~> check {
        responseAs[String] must contain("google")
      }
      Get("/v1/page?url=https://google.com") ~> myRoute ~> check {
        responseAs[String] must contain("google")
      }
    }

    "fail on a non-existing page " in {
      Get("/v1/page?url=http://www.google.com/abcedf") ~> myRoute ~> check {
        status === BadRequest
        responseAs[String] must contain("404: Not Found")
      }
    }

    "fetch a page raw" in {
      Get("/v1/page?url=http://google.com&raw=true") ~> myRoute ~> check {
        responseAs[String] must contain("href=\"/")
      }
    }

    "fetch a page cooked (no relative links)" in {
      Get("/v1/page?url=http://dekkr.nl") ~> myRoute ~> check {
        responseAs[String] must not contain "href=\"/"
      }
    }


  }
  */
}