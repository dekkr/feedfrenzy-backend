package nl.dekkr.feedfrenzy.backend.extractor.action

import java.time.format.{DateTimeFormatterBuilder, DateTimeFormatter, DateTimeParseException}
import java.time.{LocalDate, LocalTime, OffsetDateTime, ZoneOffset}
import java.util.Locale

import nl.dekkr.feedfrenzy.backend.model.DateParser


class ParseDateAction extends BaseAction {

  def execute(vars : VariableMap, a: DateParser): List[String] = {
    val inputVar = getVariable(a.inputVariable, vars)
    try {
      val locale: Locale = new Locale(a.locale)
      val formatter = createFormatter(a.pattern, locale)
      inputVar map { inputVar =>
        try {
          val date = LocalDate.parse(inputVar, formatter)
          val time = try {
            LocalTime.parse(inputVar, formatter)
          } catch {
            case e: DateTimeParseException =>
              if (a.padTime) {
                LocalTime.now()
              } else {
                LocalTime.of(0, 0)
              }
          }
          OffsetDateTime.of(date, time, ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch {
          case e: DateTimeParseException =>
            val response = s"ERROR: ${e.getMessage} - Format [${a.pattern}], locale [${a.locale}]"
            logger.warn(response)
            response
          case e: NullPointerException =>
            val response = s"ERROR: Empty input date - ${e.getMessage}"
            logger.warn(response)
            response
        }
      }
    } catch {
      case e: IllegalArgumentException => logger.warn(s"Could not create date format from format [${a.pattern}] and locale [${a.locale}] - ${e.getMessage}")
        inputVar
    }
  }

  /** *
    * Need a case-insensitive parser
    * @param pattern - Pattern to use for parsing
    * @param locale - The locale to be used
    */
  private def createFormatter(pattern: String, locale: Locale): DateTimeFormatter  =
    new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .appendPattern(pattern)
        .toFormatter
      .withLocale(locale)

}
