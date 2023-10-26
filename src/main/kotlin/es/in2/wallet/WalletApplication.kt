package es.in2.wallet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WalletCreationApplication

fun main(args: Array<String>) {
    runApplication<WalletCreationApplication>(*args)
}
