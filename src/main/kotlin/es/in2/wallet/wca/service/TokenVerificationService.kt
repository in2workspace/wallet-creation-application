package es.in2.wallet.wca.service

fun interface TokenVerificationService {
    fun verifySiopAuthRequestAsJwsFormat(requestToken: String)
}