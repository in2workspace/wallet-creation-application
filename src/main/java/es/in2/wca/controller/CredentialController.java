package es.in2.wca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Credentials", description = "Credential management API")
@Slf4j
@RestController
@RequestMapping("/api/v2/credentials")
@RequiredArgsConstructor
public class CredentialController {

    // todo: get credential using nonce of CredentialResponse

}
