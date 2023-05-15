<img alt="FactSet" src="https://www.factset.com/hubfs/Assets/images/factset-logo.svg" height="56" width="290">

# FactSet Trading streaming client library for Java

[![Maven Central](https://img.shields.io/maven-central/v/com.factset.sdk.streaming/factsettrading)](https://search.maven.org/artifact/com.factset.streaming/factsettrading)

[![Apache-2 license](https://img.shields.io/badge/license-Apache2-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

This repository contains a streaming api client for the FactSet Trading API

## Requirements

* Java JDK >= 1.8

## Installation

### Maven

Add the below dependency to the project's POM:

```xml

<dependency>
    <groupId>com.factset.sdk.streaming</groupId>
    <artifactId>factsettrading</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add these dependencies to your project's build file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.factset.sdk.streaming:factsettrading:1.0.0"
}
```

## Usage

1. [Generate OAuth 2.0 authentication credentials](https://developer.factset.com/learn/authentication-oauth2).
2. Setup Java environment.
    1. Install and activate Java 1.8+
    2. Install  [gradle](https://gradle.org/install/)
3. [Install dependencies](#installation).
4. Run the following code:

```java
package com.factset.sdk.console;

import com.factset.sdk.streaming.client.Subscription;
import com.factset.sdk.streaming.client.WebsocketApiClient;
import com.factset.sdk.streaming.factsettrading.OrderUpdateApi;
import com.factset.sdk.utils.authentication.ConfidentialClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console {

    private static final Logger logger = LoggerFactory.getLogger("main");

    public static void main(String[] args) throws Exception {
        ConfidentialClient confidentialClient = new ConfidentialClient("/path/to/config/file");

        // initialize the websocket client
        WebsocketApiClient client = new WebsocketApiClient(
                WebsocketApiClient.Options.builder()
                        .url("https://api.factset.com/trading/ems/v1")
                        .authorizer(confidentialClient)
                        .build()
        ).connectAsync().join();

        // initialize the order update api
        OrderUpdateApi api = new OrderUpdateApi(client);

        // subscribe to order updates
        Subscription subscription = api.subscribeOrderUpdates((update, t) -> {
            if (t != null) {
                logger.warn("something went wrong: {}", t.getMessage());
            } else {
                logger.info("order update: {}", update);
            }
        }).join();

        // wait
        Thread.sleep(10000);

        // cancel the subscription
        subscription.cancel();

        // close the websocket connection        
        client.disconnectAsync().join();
    }
}
```

## Contributing

Please refer to the [contributing guide](CONTRIBUTING.md).

## Logging

All logger names start with "com.factset".

This library uses [SLF4J](https://www.slf4j.org/) as logging interface,
which requires a [binding](https://www.slf4j.org/manual.html#swapping) to your logging framework on the classpath.

If no binding is found, SLF4J prints out the following warning and then defaults to a no-operation
implementation, which discard all logs:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

## Installation into local maven repo

```
./gradlew publisToMavenLocal
```

## Troubleshooting

... TODO ...

## Copyright

Copyright 2023 FactSet Research Systems Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.




