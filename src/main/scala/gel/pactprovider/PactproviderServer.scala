package gel.pactprovider

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import gel.pactprovider.PactproviderRoutes.VariantQuery
import gel.pactprovider.variants.VariantResponse
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object PactproviderServer {


  def streamWithDao[F[_] : ConcurrentEffect](variantClient: VariantQuery => F[VariantResponse])(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    val httpApp = PactproviderRoutes.variantRoutes(variantClient)
      .orNotFound
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)
    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain


  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {


    // The Real Client Goes here
    val realClient: VariantQuery => F[VariantResponse]
    = _ => new VariantResponse("caseId1", "benign").pure[F]


    val httpApp = PactproviderRoutes.variantRoutes(realClient)
      .orNotFound

    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}