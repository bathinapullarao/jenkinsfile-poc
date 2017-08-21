package com.techcoaster.pipeline.jenkinsfilepoc

import org.springframework.web.bind.annotation.RestController


@RestController
class HelloWorldController {

    String hello() {
        return "Hello World TEST"
    }
}
