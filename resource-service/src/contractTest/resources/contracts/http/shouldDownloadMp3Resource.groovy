package contracts.http

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("should download mp3 resource")
    request {
        method GET()
        url "/resources/1"
        headers {
            accept("audio/mpeg")
        }
    }
    response {
        status OK()
        headers {
            header("Content-Type", "audio/mpeg")
        }
        body("MP3-BINARY-PLACEHOLDER")
    }
}
