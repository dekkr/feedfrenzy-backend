package nl.dekkr.feedfrenzy.backend.model

import org.apache.commons.validator.routines.UrlValidator
/**
 * Entity for the page request
 */
case class PageUrl(url : String, maxAge : Option[Int] = Some(1440), raw : Option[Boolean] = None) {
  require(!url.isEmpty, "url must not be empty")
  require(new UrlValidator(Array("http","https")).isValid(url), "url is invalid")
}
