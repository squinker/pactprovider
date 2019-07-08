package gel.pactprovider




import cats.effect.Sync
import cats.implicits._
import gel.pactprovider.variants.VariantResponse
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object PactproviderRoutes {

  case class VariantQuery(chromosome: String, start: String, end: String)

  def variantRoutes[F[_] : Sync](variantClient: VariantQuery => F[VariantResponse]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "chromosome" / chromosome / "start" / start / "end" / end =>

        val query = new VariantQuery(chromosome, start, end)

        for {
          variantResponse <- variantClient.apply(query)
          response        <- Ok(variantResponse)
        } yield response
    }
  }


}
