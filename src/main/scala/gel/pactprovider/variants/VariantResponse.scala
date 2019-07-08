package gel.pactprovider.variants

import cats.Applicative
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf


case class VariantQuery(chromosome: String, start: String, end: String);

object VariantResponse {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val fooDecoder: Decoder[VariantResponse] = deriveDecoder[VariantResponse]

  implicit val fooEncoder: Encoder[VariantResponse] = deriveEncoder[VariantResponse]

  implicit def greetingEntityEncoder[F[_] : Applicative]: EntityEncoder[F, VariantResponse] =
    jsonEncoderOf[F, VariantResponse]
}

case class VariantResponse(caseId: String, pathogenicity: String);
