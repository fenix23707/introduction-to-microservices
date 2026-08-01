package contracts.http

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("should return not found when resource is missing")
    request {
        method GET()
        url "/resources/999"
        headers {
            accept("audio/mpeg, application/json")
        }
    }
    response {
        status NOT_FOUND()
        body(
            errorCode: "404",
            errorMessage: "Resource with ID=999 not found"
        )
        headers {
            contentType(applicationJson())
        }
    }
}
