package es.in2.wallet.service.impl

import es.in2.wallet.exception.IssuerNameAlreadyExistsException
import es.in2.wallet.model.AppIssuerData
import es.in2.wallet.repository.AppIssuerDataRepository
import es.in2.wallet.service.AppIssuerDataService
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Service
class AppIssuerDataServiceImpl(
        private val appIssuerDataRepository: AppIssuerDataRepository,
) : AppIssuerDataService {

    private val log: Logger = LoggerFactory.getLogger(AppUserServiceImpl::class.java)
    override fun saveIssuerData(issuerName: String, issuerMetadata: String) {
        try {
            checkIfIssuerNameAlreadyExist(issuerName)
            val appIssuerData = AppIssuerData(
                id = UUID.randomUUID(),
                name = issuerName,
                metadata = issuerMetadata
            )
            log.debug("AppIssuerDataServiceImpl.saveIssuerData()")
            appIssuerDataRepository.save(appIssuerData)
        }catch (e: IssuerNameAlreadyExistsException){
            log.info("This data is already save")
        }
    }

    override fun getIssuerDataByIssuerName(issuerName: String): Optional<AppIssuerData> {
        log.info("AppIssuerDataServiceImpl.getUserByUsername()")
        return appIssuerDataRepository.findAppIssuerDataByName(issuerName)
    }
    private fun checkIfIssuerNameAlreadyExist(issuerName: String) {
        log.info("AppIssuerDataServiceImpl.checkIfIssuerNameAlreadyExist()")
        if (getIssuerDataByIssuerName(issuerName).isPresent) {
            throw IssuerNameAlreadyExistsException("Issuer name already exists: $issuerName")
        }
    }
}