package es.in2.wca.service;

import es.in2.wca.domain.AuthorizationRequest;
import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.VcSelectorResponse;
import es.in2.wca.domain.CredentialsBasicInfo;
import id.walt.credentials.w3c.PresentableCredential;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WalletDataService {
    Mono<CredentialResponse> saveCredential(String processId, String authorizationToken, CredentialResponse credentialResponse);

    Mono<List<CredentialsBasicInfo>> getSelectableVCsByAuthorizationRequestScope(String processId, String authorizationToken, AuthorizationRequest authorizationRequest);
    Mono<List<PresentableCredential>> getVerifiableCredentials(String processId, String authorizationToken, VcSelectorResponse vcSelectorResponse);
}
