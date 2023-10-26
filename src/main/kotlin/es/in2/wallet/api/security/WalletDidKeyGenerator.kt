package es.in2.wallet.api.security

class WalletDidKeyGenerator(private val didKey: String) {

    fun getDidKey(): String {
        return didKey
    }

}