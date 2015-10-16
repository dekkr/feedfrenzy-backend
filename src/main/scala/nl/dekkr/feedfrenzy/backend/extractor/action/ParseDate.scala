package nl.dekkr.feedfrenzy.backend.extractor.action

import java.text.ParseException
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{LocalDate, LocalTime, OffsetDateTime, ZoneOffset}
import java.util.Locale

import nl.dekkr.feedfrenzy.backend.model.DateParser


class ParseDate extends BaseAction {

  def execute(vars : VariableMap, a: DateParser): List[String] = {
    val inputVar = getVariable(a.inputVariable, vars)
    try {
      val locale: Locale = new Locale(a.locale)
      val formatter = DateTimeFormatter.ofPattern(a.pattern).withLocale(locale)
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
          case e: ParseException =>
            val response = s"ERROR: Could not parse date string [$inputVar] with format [${a.pattern}] and locale [${a.locale}] - ${e.getMessage}"
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

}
