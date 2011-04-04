package allaboard
import org.jboss.netty.channel.{Channel,ChannelFuture,ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.{HttpHeaders, DefaultHttpChunk,
                                           DefaultHttpChunkTrailer}
import org.jboss.netty.buffer.ChannelBuffers
import util.control.Exception.allCatch
import com.gent.departurevision.DepartureVision
import unfiltered.{request,response,netty}
import response._

class Stream(station: String) extends Thread {
  import Stream._
  val empty = Set.empty[DepartureVision.Departure]
  @volatile private var prior = empty
  private val clients = new DefaultChannelGroup
  override def run() {
    while (!plsfinish) {
      if (!clients.isEmpty()) {
        val latest = allCatch.either { 
          DepartureVision.departures(station){ empty ++ _ }
        }.fold({ e => 
          println("Exception on station %s: %s".format(station, e.getMessage))
          empty
        }, identity)
        Chunker(latest -- prior)(clients.write)
        prior = latest
      } else {
        prior = Set.empty
      }
      Thread.sleep(10000)
    }
    clients.write(
      new DefaultHttpChunkTrailer
    ).await()
  }
  def add(req: request.HttpRequest[netty.ReceivedMessage]) {
    val ch = req.underlying.event.getChannel
    val initial = req.underlying.defaultResponse(ChunkedJson)
    ch.write(initial).addListener { () =>
      Chunker(prior)(ch.write)
      clients.add(ch)
    }
    if (!isAlive) start()
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
