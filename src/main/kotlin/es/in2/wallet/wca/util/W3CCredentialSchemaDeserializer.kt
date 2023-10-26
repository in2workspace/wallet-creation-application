package es.in2.wallet.wca.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import id.walt.credentials.w3c.W3CCredentialSchema

class W3CCredentialSchemaDeserializer : JsonDeserializer<W3CCredentialSchema>() {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): W3CCredentialSchema {
        val node: JsonNode = parser.codec.readTree(parser)
        return W3CCredentialSchema.fromJson(node.toString())
    }
}