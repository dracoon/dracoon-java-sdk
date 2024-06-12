[![Build](https://github.com/dracoon/dracoon-java-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/dracoon/dracoon-java-sdk/actions/workflows/build.yml)
[![Unit Tests](https://github.com/dracoon/dracoon-java-sdk/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/dracoon/dracoon-java-sdk/actions/workflows/unit-tests.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dracoon/dracoon-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dracoon/dracoon-sdk)
# Dracoon Java SDK

A library to access the Dracoon REST API.

## Setup

#### Minimum Requirements

Java 11 or newer

#### Download

##### Maven

Add this dependency to your pom.xml:
```xml
<dependency>
    <groupId>com.dracoon</groupId>
    <artifactId>dracoon-sdk</artifactId>
    <version>4.0.1</version>
</dependency>
```

##### Gradle

Add this dependency to your build.gradle:
```groovy
compile 'com.dracoon:dracoon-sdk:4.0.1'
```

##### JAR import

The latest JAR can be found [here](https://github.com/dracoon/dracoon-java-sdk/releases).

Note that you also need to include the following dependencies:
1. Bouncy Castle PKIX/CMS/...: https://mvnrepository.com/artifact/org.bouncycastle/bcpkix-jdk18on
2. Bouncy Castle Provider: https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
3. Bouncy Castle Utils: https://mvnrepository.com/artifact/org.bouncycastle/bcutil-jdk18on
4. Dracoon Crypto SDK: https://mvnrepository.com/artifact/com.dracoon/dracoon-crypto-sdk
5. Google Gson: https://mvnrepository.com/artifact/com.google.code.gson/gson
6. Square OkHttp: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
7. Square OkIo: https://mvnrepository.com/artifact/com.squareup.okio/okio
8. Square Retrofit: https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
9. Square Retrofit Gson Converter: https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson

#### Usage on Android

The Android platform ships with a cut-down version of Bouncy Castle. In the past (pre-Android 3.0),
this caused conflicts and there was a separate version of the SDK for Android which used Spongy
Castle.

Because there are very few people who use pre-Android 3.0 devices, and the fact that Spongy Castle
is not maintained anymore, there is no longer a separate version.

To avoid problems you should reinitialize the Bouncy Castle security provider when your application
starts. This can be done by extending `Application` and using a static initialization block. See
following example.

```java
...

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DracoonApplication extends Application {
    
    static {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());
    }

    ...
    
}
```

## Example

The following example shows how to get all root rooms.

```java
DracoonAuth auth = new DracoonAuth("access-token");

DracoonClient client = new DracoonClient.Builder(new URL("https://dracoon.team"))
        .auth(auth)
        .build();

long parentNodeId = 0L;

NodeList nodeList = client.nodes().getNodes(parentNodeId);
for (Node node : nodeList.getItems()) {
    System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
}
```

## Documentation

The documentation of the Dracoon SDK can be found [here](doc/main.md).

## Contribution

If you would like to contribute code, fork the repository and send a pull request. When submitting
code, please make every effort to follow existing conventions and style in order to keep the code as
readable as possible.

## Copyright and License

Copyright Dracoon GmbH. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.