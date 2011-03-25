package com.example

import unfiltered.request._
import unfiltered.response._

import org.clapper.avsl.Logger

/** unfiltered plan */
class App extends unfiltered.netty.cycle.Plan {
  import QParams._
  
  val logger = Logger(classOf[App])
  
  def intent = {
    case GET(Path(p)) => 
      logger.debug("GET %s" format p)
      view(Map.empty)(<p> What say you? </p>)
    case POST(Path(p) & Params(params)) =>
      logger.debug("POST %s" format p)
      val vw = view(params)_
      val expected = for { 
        int <- lookup("int") is
          int { s => "'%s' is not an integer".format(s) } is
          required("missing int")
        word <- lookup("palindrome") is
          trimmed is 
          nonempty("Palindrome is empty") is
          pred(palindrome, { s =>
            "%s is not a palindrome".format(s)
          }) is
          required("missing palindrome")
      } yield vw(<p>Yup. { int.get } is an integer and { word.get } is a palindrome. </p>)
      expected(params) orFail { fails =>
        vw(<ul> { fails.map { f => <li>{f.error} </li> } } </ul>)
      }
  }
  def palindrome(s: String) = s.toLowerCase.reverse == s.toLowerCase
  def view(params: Map[String, Seq[String]])(body: scala.xml.NodeSeq) = {
    def p(k: String) = params.get(k).flatMap { _.headOption } getOrElse("")
    Html(
     <html><body>
       { body }
       <form method="POST">
         Integer <input name="int" value={ p("int") } ></input>
         Palindrome <input name="palindrome" value={ p("palindrome") } />
         <input type="submit" />
       </form>
     </body></html>
   )
  }
}

/** embedded server */
object Server {
  val logger = Logger(Server.getClass)
  
  def main(args: Array[String]) {
    logger.info("starting unfiltered app at localhost on port %s" format 8080)
    unfiltered.netty.Http(8080).handler(new App).run
  }
}
