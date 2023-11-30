package es.in2.wca.service.impl;

import es.in2.wca.domain.QrType;
import es.in2.wca.exception.NoSuchQrContentException;
import es.in2.wca.facade.AttestationExchangeServiceFacade;
import es.in2.wca.facade.CredentialIssuanceServiceFacade;
import es.in2.wca.service.QrCodeProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static es.in2.wca.domain.QrType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QrCodeProcessorServiceImpl implements QrCodeProcessorService {

    private final CredentialIssuanceServiceFacade credentialIssuanceServiceFacade;
    private final AttestationExchangeServiceFacade attestationExchangeServiceFacade;

    @Override
    public Mono<Object> processQrContent(String processId, String authorizationToken, String qrContent) {
        log.debug("ProcessID: {} - Processing QR content: {}", processId, qrContent);
        return identifyQrContentType(qrContent)
                .flatMap(qrType -> {
                    switch (qrType) {
                        case CREDENTIAL_OFFER_URI: {
                            log.info("ProcessID: {} - Processing a Verifiable Credential Offer URI", processId);
                            return credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)
                                    .doOnSuccess(credential -> log.info("ProcessID: {} - Credential Issued: {}", processId, credential))
                                    .doOnError(e -> log.error("ProcessID: {} - Error while issuing credential: {}", processId, e.getMessage()));
                        }
                        case OPENID_CREDENTIAL_OFFER: {
                            log.info("ProcessID: {} - Processing a Verifiable Credential Offer URI in EBSI Format", processId);
                            return credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)
                                    .doOnSuccess(credential -> log.info("ProcessID: {} - Credential Issued: {}", processId, credential))
                                    .doOnError(e -> log.error("ProcessID: {} - Error while issuing credential: {}", processId, e.getMessage()));
                        }
                        case VC_LOGIN_REQUEST: {
                            log.info("ProcessID: {} - Processing a Verifiable Credential Login Request", processId);
                            return attestationExchangeServiceFacade.getSelectableCredentialsRequiredToBuildThePresentation(processId, authorizationToken, qrContent)
                                    .doOnSuccess(credential -> log.info("ProcessID: {} - Attestation Exchange", processId))
                                    .doOnError(e -> log.error("ProcessID: {} - Error while processing Attestation Exchange: {}", processId, e.getMessage()));
                        }
                        case OPENID_AUTHENTICATION_REQUEST: {
                            log.info("ProcessID: {} - Processing an Authentication Request", processId);
                            return Mono.error(new NoSuchQrContentException("OpenID Authentication Request not implemented yet"));
                        }
                        case UNKNOWN: {
                            String errorMessage = "The received QR content cannot be processed";
                            log.warn(errorMessage);
                            return Mono.error(new NoSuchQrContentException(errorMessage));
                        }
                        default: {
                            return Mono.empty();
                        }
                    }
                });
    }

    private Mono<QrType> identifyQrContentType(String qrContent) {
        return Mono.fromSupplier(() -> {
            if (qrContent.matches("(https|http).*?(authentication-request|authentication-requests).*")) {
                return VC_LOGIN_REQUEST;
            } else if (qrContent.matches("(https|http).*?(credential-offer).*")) {
                return QrType.CREDENTIAL_OFFER_URI;
            } else if (qrContent.matches("openid-credential-offer://.*")) {
                return OPENID_CREDENTIAL_OFFER;
            } else if (qrContent.matches("openid://.*")) {
                return OPENID_AUTHENTICATION_REQUEST;
            } else {
                log.warn("Unknown QR content type: {}", qrContent);
                return UNKNOWN;
            }
        });
    }

}
