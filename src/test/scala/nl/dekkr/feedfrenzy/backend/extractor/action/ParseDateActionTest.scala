package nl.dekkr.feedfrenzy.backend.extractor.action

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import nl.dekkr.feedfrenzy.backend.model.DateParser
import org.scalatest.FlatSpecLike


class ParseDateActionTest extends FlatSpecLike {

  val PDA = new ParseDateAction()

  val expectedResults = List("2015-10-12T00:00:00Z", "2014-01-03T00:00:00Z", "2016-02-29T00:00:00Z")


  "ParseDateAction" should "be case insensitive" in {
    val locale = "en"
    val dates = List("October 12, 2015", "January 3, 2014", "February 29, 2016")
    val vars = Map("mixed" -> dates, "lower" -> dates.map(_.toLowerCase), "upper" -> dates.map(_.toUpperCase))

    val actionMixed = new DateParser(inputVariable = Some("mixed"), outputVariable = Some("r1"), pattern = "MMMM d, yyyy", locale = locale, padTime = false)
    assert(PDA.execute(vars, actionMixed) == expectedResults, "Failed on mixed case")

    val actionLower = actionMixed.copy(inputVariable = Some("lower"))
    assert(PDA.execute(vars, actionLower) == expectedResults, "Failed on lower case")

    val actionUpper = actionMixed.copy(inputVariable = Some("upper"))
    assert(PDA.execute(vars, actionUpper) == expectedResults, "Failed on upper case")
  }

  it should "make a distinction between different locales" in {
    val locale = "nl"
    val dates = List("Oktober 12, 2015", "Januari 3, 2014", "Februari 29, 2016")
    val vars = Map("mixed" -> dates, "lower" -> dates.map(_.toLowerCase), "upper" -> dates.map(_.toUpperCase))

    val actionMixed = new DateParser(inputVariable = Some("mixed"), outputVariable = Some("r1"), pattern = "MMMM d, yyyy", locale = locale, padTime = false)
    assert(PDA.execute(vars, actionMixed) == expectedResults, "Failed on mixed case")

    val actionLower = actionMixed.copy(inputVariable = Some("lower"))
    assert(PDA.execute(vars, actionLower) == expectedResults, "Failed on lower case")

    val actionUpper = actionMixed.copy(inputVariable = Some("upper"))
    assert(PDA.execute(vars, actionUpper) == expectedResults, "Failed on upper case")
  }

  it should "support different patterns" in {
    testPattern("en", "2015-10-12T00:00:00Z", "October 12, 2015", "MMMM d, yyyy")
    testPattern("en", "2015-10-12T00:00:00Z", "Oct 12, 2015", "MMM d, yyyy")
    testPattern("en", "2015-10-12T00:00:00Z", "Oct 12 in the year 2015", "MMM d 'in the year' yyyy")

    testPattern("nl", "2015-10-12T00:00:00Z", "12 okt 15", "dd MMM yy")
    testPattern("nl", "2015-10-12T00:00:00Z", "12-10-2015", "dd-MM-yyyy")
    testPattern("nl", "2015-10-12T13:04:17Z", "12-10-2015 13:04:17", "dd-MM-yyyy HH:mm:ss")
    testPattern("nl", "2015-10-12T01:04:17Z", "12-10-2015 1:04:17", "dd-MM-yyyy H:mm:ss")
  }

  it should "pad the time" in {
    val date = "12 okt 2015"
    val pattern = "dd MMM yyyy"
    val locale = "nl"

    val actionLongFormat = new DateParser(inputVariable = Some("d"), outputVariable = Some("r1"), pattern = pattern, locale = locale, padTime = true)
    val result = PDA.execute(Map("d" -> List(date)), actionLongFormat)

    val resultTime = result.head.substring(11)
    val time = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    assert(resultTime.startsWith(time))
  }

  it should "return a empty list if the input is an empty list" in {
    val pattern = "dd MMM yyyy"
    val locale = "nl"

    val actionLongFormat = new DateParser(inputVariable = Some("d"), outputVariable = Some("r1"), pattern = pattern, locale = locale, padTime = true)
    val result = PDA.execute(Map("d" -> List.empty), actionLongFormat)
    assert(result.isEmpty)
  }

  it should "return a an error message in the result if the input is a list with a empty string" in {
    val pattern = "dd MMM yyyy"
    val locale = "nl"

    val actionLongFormat = new DateParser(inputVariable = Some("d"), outputVariable = Some("r1"), pattern = pattern, locale = locale, padTime = true)
    val result = PDA.execute(Map("d" -> List("")), actionLongFormat)
    assert(result == List(s"ERROR: Text '' could not be parsed at index 0 - Format [$pattern], locale [$locale]"))
  }


  it should "return the input if the pattern is invalid" in {
    val date = "12 okt 2015"
    val pattern = "invalid-pattern"
    val locale = "nl"

    val actionLongFormat = new DateParser(inputVariable = Some("d"), outputVariable = Some("r1"), pattern = pattern, locale = locale, padTime = true)
    val result = PDA.execute(Map("d" -> List(date)), actionLongFormat)
    assert(result == List(date))
  }



  def testPattern(locale: String, expectedResult: String, date: String, pattern: String): Unit = {
    val actionLongFormat = new DateParser(inputVariable = Some("d"), outputVariable = Some("r1"), pattern = pattern, locale = locale, padTime = false)
    assert(PDA.execute(Map("d" -> List(date)), actionLongFormat) == List(expectedResult))
  }

}
