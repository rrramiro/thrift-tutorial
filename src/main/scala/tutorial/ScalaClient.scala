package tutorial

import cats.effect.{Async, IO}
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport._

import scala.language.higherKinds

object ScalaClient extends App {
  def initializeTransport[F[_]](implicit async: Async[F]): F[TTransport] = args.headOption match {
    case Some("simple") => async.delay {
      val transport = new TSocket("localhost", 9090)
      transport.open()
      transport
    }
    case Some("secure") => async.delay{
      val params = new TSSLTransportFactory.TSSLTransportParameters
      params.setTrustStore(".truststore", "thrift", "SunX509", "JKS")
      TSSLTransportFactory.getClientSocket("localhost", 9094, 0, params)
    }
    case Some("http") => async.delay{
      val url = "http://localhost:9000/calculator"
      new THttpClient(url)
    }
    case param => async.raiseError(new Exception(s"Wrong transport parameter: $param"))
  }

  val work1 = new Work

  work1.op = Operation.DIVIDE
  work1.num1 = 1
  work1.num2 = 0

  val work2 = new Work
  work2.op = Operation.SUBTRACT;
  work2.num1 = 15;
  work2.num2 = 10;

  val result = {
    for{
      transport <- initializeTransport[IO]
      client = new CalculatorClient[IO](new TBinaryProtocol(transport))
      _ <- client.ping().map{_ => println("ping()")}
      _ <- client.add(1,1).map{sum => println(s"1+1=$sum")}
      _ <- client.calculate(1, work1).redeem(f =>println(s"Invalid operation: $f"), _ => ())
      _ <- client.calculate(1, work2).map{diff => println(s"15-10=$diff")}
      _ <- client.getStruct(1).map{log => println(s"Check log: ${log.value}")}
    } yield ()
  }

  result.unsafeRunSync()
}
