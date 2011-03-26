package allaboard

import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.DefaultHttpChunk
import org.jboss.netty.buffer.ChannelBuffers

object Broadcast {
  val clients = new DefaultChannelGroup

  def apply() {
    clients.write(new DefaultHttpChunk(
      ChannelBuffers.copiedBuffer("ohhi\n".getBytes("utf-8"))
    ))
  }
}
