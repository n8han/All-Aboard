package allaboard

import unfiltered.request._
import unfiltered.response._

import util.control.Exception.allCatch

/** unfiltered plan */
class App extends unfiltered.netty.channel.Plan {
  def intent = {
    case req @ GET(Path(Seg(station :: Nil))) =>
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
        Stream.stop()
      }
      .run()
  }
}
