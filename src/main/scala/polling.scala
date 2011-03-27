package allaboard

import com.gent.departurevision.DepartureVision

object Polling extends Thread {
  @volatile private var prior = Set.empty[DepartureVision.Departure]
  def departures = prior
  private var plsfinish = false
  def finish() { plsfinish = true }
  override def run() {
    while (!plsfinish) {
      val latest = DepartureVision.departures("ny")(
        Set.empty[DepartureVision.Departure] ++ _
      )
      Broadcast.write(prior -- latest)
      prior = latest
      Thread.sleep(10000)
    }
  }
}
