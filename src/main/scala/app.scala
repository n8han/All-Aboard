package allaboard

import unfiltered.request._

import util.control.Exception.allCatch

/** unfiltered plan */
class App extends unfiltered.netty.channel.Plan {
  def intent = {
    case req @ GET(Path(Seg(station :: Nil))) =>
      req.underlying.respond(
        unfiltered.response.Redirect("/njt/" + station)
      )
    case req @ GET(Path(Seg("njt" :: Station(station) :: Nil))) =>
      Stream(station).add(req)
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    val port = args.headOption.flatMap(arg =>
      allCatch.opt(arg.toInt)
    ).getOrElse(7979)

    unfiltered.netty.Http(port)
      .handler(new App)
      .beforeStop { 
        println("Waiting for all streams to finish...")
        Stream.stop()
      }
      .run()
  }
}
