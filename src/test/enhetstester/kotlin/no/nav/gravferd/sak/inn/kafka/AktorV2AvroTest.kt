package no.nav.gravferd.sak.inn.kafka

import no.nav.person.pdl.aktor.v2.Aktor
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class AktorV2AvroTest {
    @Test
    fun `generert Aktor-skjema støtter round-trip av generiske Avro-data`() {
        val schema = Aktor.getClassSchema()
        val identifikatorerSchema = schema.getField("identifikatorer").schema()
        val identifikatorSchema = identifikatorerSchema.elementType
        val typeSchema = identifikatorSchema.getField("type").schema()

        fun identifikator(
            idnummer: String,
            type: String,
            gjeldende: Boolean,
        ): GenericRecord =
            GenericData.Record(identifikatorSchema).apply {
                put("idnummer", idnummer)
                put("type", GenericData.EnumSymbol(typeSchema, type))
                put("gjeldende", gjeldende)
            }

        val aktor =
            GenericData.Record(schema).apply {
                put(
                    "identifikatorer",
                    listOf(
                        identifikator("syntetisk-gjeldende-id", "FOLKEREGISTERIDENT", true),
                        identifikator("syntetisk-historisk-id", "AKTORID", false),
                    ),
                )
            }
        val output = ByteArrayOutputStream()
        val encoder = EncoderFactory.get().binaryEncoder(output, null)
        GenericDatumWriter<GenericRecord>(schema).write(aktor, encoder)
        encoder.flush()

        val decoder = DecoderFactory.get().binaryDecoder(output.toByteArray(), null)
        val lest = GenericDatumReader<GenericRecord>(schema).read(null, decoder)
        val identifikatorer = lest.get("identifikatorer") as List<GenericRecord>

        assertThat(identifikatorer).hasSize(2)
        assertThat(identifikatorer[0].get("idnummer").toString()).isEqualTo("syntetisk-gjeldende-id")
        assertThat(identifikatorer[0].get("type").toString()).isEqualTo("FOLKEREGISTERIDENT")
        assertThat(identifikatorer[0].get("gjeldende")).isEqualTo(true)
        assertThat(identifikatorer[1].get("idnummer").toString()).isEqualTo("syntetisk-historisk-id")
        assertThat(identifikatorer[1].get("type").toString()).isEqualTo("AKTORID")
        assertThat(identifikatorer[1].get("gjeldende")).isEqualTo(false)
    }

    // TODO: Separate tester med SpecificDatumReader må på plass når eksplisitt tillit til genererte klasser er avklart.
}
