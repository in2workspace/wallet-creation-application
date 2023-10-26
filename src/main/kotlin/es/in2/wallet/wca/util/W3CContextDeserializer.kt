package es.in2.wallet.wca.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import id.walt.credentials.w3c.W3CContext

class W3CContextDeserializer : StdDeserializer<W3CContext>(W3CContext::class.java) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): W3CContext {
        val node: JsonNode = parser.codec.readTree(parser)
        return W3CContext.fromJson(node.toString())
    }
}