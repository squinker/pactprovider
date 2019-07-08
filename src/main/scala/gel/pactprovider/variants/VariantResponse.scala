package gel.pactprovider.variants


case class VariantQuery(chromosome: String, start: String, end: String);

case class VariantResponse(caseId: String, pathogenicity: String);
