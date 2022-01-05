package tutorial

import shared.SharedStruct
import cats.effect.Async
import org.apache.thrift.protocol.TProtocol

import scala.language.higherKinds

class CalculatorClient[F[_]](protocol: TProtocol)(implicit async: Async[F]) {
   private val client = new Calculator.Client(protocol)

   def ping(): F[Unit] = async.delay {
      client.ping()
   }

   def add(num1: Int, num2: Int): F[Int] = async.delay {
      client.add(num1, num2)
   }

   def calculate(logid: Int, w: Work): F[Int] = async.delay {
      client.calculate(logid, w)
   }

   def zip(): F[Unit] = async.delay {
      client.zip()
   }

   def getStruct(key: Int): F[SharedStruct] = async.delay {
      client.getStruct(key)
   }
}
