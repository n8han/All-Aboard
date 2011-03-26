package allaboard

object Polling extends Thread {
  private var plsfinish = false
  def finish() { plsfinish = true }
  override def run() {
    while (!plsfinish) {
      Broadcast()
      Thread.sleep(5000)
    }
  }
}
