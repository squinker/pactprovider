package gel.pactprovider

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import gel.pactprovider.PactproviderRoutes.VariantQuery
import gel.pactprovider.variants.VariantResponse
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object PactproviderServer {


  def streamWithDao[F[_] : ConcurrentEffect](variantClient: VariantQuery => F[VariantResponse] )(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream


      
      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = PactproviderRoutes.variantRoutes(variantClient)
        .orNotFound


      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain


  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {




    for {
      client <- BlazeClientBuilder[F](global).stream


      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = PactproviderRoutes.variantRoutes(request => new VariantResponse("caseId1", "benign").pure[F])
        .orNotFound


      // With Middlewares in place
      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}