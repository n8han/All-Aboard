package allaboard
import org.jboss.netty.channel.{Channel,ChannelFuture,ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.{HttpHeaders, DefaultHttpChunk,
                                           DefaultHttpChunkTrailer}
import org.jboss.netty.buffer.ChannelBuffers
import com.gent.departurevision.DepartureVision
import unfiltered.{request,response,netty}
import response._

class Stream(station: String) extends Thread {
  import Stream._
  @volatile private var prior = Set.empty[DepartureVision.Departure]
  private val clients = new DefaultChannelGroup
  override def run() {
    while (!plsfinish) {
      val latest = DepartureVision.departures(station)(
        Set.empty[DepartureVision.Departure] ++ _
      )
      Chunker(prior -- latest)(clients.write)
      prior = latest
      Thread.sleep(10000)
    }
    clients.write(
      new DefaultHttpChunkTrailer
    ).awaitUninterruptibly()
  }
  def add(req: request.HttpRequest[netty.ReceivedMessage]) {
    val ch = req.underlying.event.getChannel
    val initial = req.underlying.defaultResponse(ChunkedJson)
    ch.write(initial).addListener { () =>
      Chunker(prior)(ch.write)
      clients.add(ch)
    }
  }
}

object Stream {
  @volatile private var stopping = false
  def plsfinish = stopping
  def stop() {
    stopping = true
    streams.values.foreach { _.join() }
  }
  @volatile private var streams = Map.empty[String, Stream]
  def apply(station: String) =
    // try first without synchronizing
    streams.getOrElse(station, {
      streams.synchronized {
        // be sure we haven't lost a race
        streams.getOrElse(station, {
          val stream = new Stream(station)
          streams = streams + (station -> stream)
          stream
        })
      }
    })

  val ChunkedJson =
    response.Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent

  implicit def block2listener[T](block: () => T): ChannelFutureListener =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }
}
