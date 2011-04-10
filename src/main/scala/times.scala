package allaboard

import org.scala_tools.time.Imports._

object Time {
  /** Given some wall clock hour and minute (current time zon assumed),
   * find the closest time to now that it could be referring to. */
  def guess(hour: Int, minute: Int, now: DateTime = DateTime.now) = {
    val times = now.withHour(hour).withMinute(minute) ::
                now.withHour((hour + 12) % 24).withMinute(minute) :: Nil
    times.flatMap { t =>
      t :: (t - 1.day) :: (t + 1.day) :: Nil
    }.sortBy { t =>
      math.abs(new Duration(now, t).seconds)
    }.head.withSecondOfMinute(0).withMillisOfSecond(0).date
  }
}
