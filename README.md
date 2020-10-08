[![Build Status](https://travis-ci.org/dracoon/dracoon-java-sdk.svg?branch=master)](https://travis-ci.org/dracoon/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dracoon/dracoon-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dracoon/dracoon-sdk)
# Dracoon Java SDK

A library to access the Dracoon REST API.

## Setup

#### Minimum Requirements

Java 6 or newer

#### Download

##### Maven

Add this dependency to your pom.xml:
```xml
<dependency>
    <groupId>com.dracoon</groupId>
    <artifactId>dracoon-sdk</artifactId>
    <version>1.8.0-beta1</version>
</dependency>
```

##### Gradle

Add this dependency to your build.gradle:
```groovy
compile 'com.dracoon:dracoon-sdk:1.8.0-beta1'
```

##### JAR import

The latest JAR can be found [here](https://github.com/dracoon/dracoon-java-sdk/releases).

Note that you also need to include the following dependencies:
1. Bouncy Castle Provider: https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
2. Bouncy Castle PKIX/CMS/...: https://mvnrepository.com/artifact/org.bouncycastle/bcpkix-jdk15on
3. Dracoon Crypto SDK: https://mvnrepository.com/artifact/com.dracoon/dracoon-crypto-sdk
4. Google Gson: https://mvnrepository.com/artifact/com.google.code.gson/gson
5. Square OkHttp: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
6. Square OkIo: https://mvnrepository.com/artifact/com.squareup.okio/okio
7. Square Retrofit: https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
8. Square Retrofit Gson Converter: https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson

#### Download for Android

The Android platform unfortunately ships with a cut-down version of Bouncy Castle. This makes it
difficult to use libraries that have an updated version of Bouncy Castle as a dependency.

To solve this issue, there is a second version of the SDK which uses Spongy Castle.

##### Maven

Add this dependency to your pom.xml:
```xml
<dependency>
    <groupId>com.dracoon</groupId>
    <artifactId>dracoon-android-sdk</artifactId>
    <version>1.8.0-beta1</version>
</dependency>
```

##### Gradle

Add this dependency to your build.gradle:
```groovy
compile 'com.dracoon:dracoon-android-sdk:1.8.0-beta1'
```

##### JAR import

The latest JAR can be found [here](https://github.com/dracoon/dracoon-java-sdk/releases).

Note that you also need to include the following dependencies:
1. Dracoon Crypto SDK: https://mvnrepository.com/artifact/com.dracoon/dracoon-android-crypto-sdk
2. Google Gson: https://mvnrepository.com/artifact/com.google.code.gson/gson
3. Spongy Castle PKIX/CMS/...: https://mvnrepository.com/artifact/com.madgag.spongycastle/pkix
4. Spongy Castle Provider: https://mvnrepository.com/artifact/com.madgag.spongycastle/prov
5. Square OkHttp: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
6. Square OkIo: https://mvnrepository.com/artifact/com.squareup.okio/okio
7. Square Retrofit: https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
8. Square Retrofit Gson Converter: https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson

#### Java JCE Setup

**IMPORTANT FOR JAVA VERSIONS 6 (<191), 7 (<181) and 8 (<162):**

You need to install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy
Files. Otherwise you'll get an exception about key length or an exception when parsing PKCS private
keys.

The Unlimited Strength Jurisdiction Policy Files can be found here:
- Java 6: https://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
- Java 7: https://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
- Java 8: https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

For Java 9 and above, the Unlimited Strength Jurisdiction Policy Files are no longer needed.
(For more information see: https://stackoverflow.com/questions/1179672)

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

If you would like to contribute code, fork the repository and send a pull request. We don't use the
GitHub Flow, so please create a feature branch of the develop branch and make your changes there.

When submitting code, please make every effort to follow existing conventions and style in order to
keep the code as readable as possible.

## Copyright and License

Copyright Dracoon GmbH. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.