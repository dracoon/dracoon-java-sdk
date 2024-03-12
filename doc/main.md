# Dracoon Java SDK Documentation

## Getting started

To get started with the Dracoon SDK, take a look at the examples in:
`/example/src/main/java/com/dracoon/sdk/example/`

## Authorization

The Dracoon SDK uses OAuth 2.0 for client authorization.

(For a detailed description see: https://tools.ietf.org/html/rfc6749)

To use the Dracoon API, you first need to register a new OAuth client. Currently, OAuth clients
can only be created, changed and deleted via the Dracoon API. See
[support article](https://support.dracoon.com/hc/en-us/articles/115003832605-OAuth-2-0-Client-Registration)
for more details.

Because implementing OAuth authorization might be a bit tricky for beginners, the Dracoon SDK
provides helper classes to handle the OAuth authorization steps to obtain and refresh tokens. The 
class `DracoonAuth` is used to configure which steps of the OAuth authorization are made by the
Dracoon SDK.

__The following three modes are supported:__

- Authorization Code Mode: This is the most common mode. Your application must request authorization
and obtain an authorization code. The retrieval of the access and refresh token, the authorization
code and the automatic token refresh is handled by the Dracoon SDK.

- Access Token Mode: This is a simple mode. You can use it during development, for terminal
applications and scripts where a specific user account is used.

- Access and Refresh Token Mode: This mode can be used to obtain access and refresh token yourself.

A example for the Authorization Code Mode can be found here:
`example/src/main/java/com/dracoon/sdk/example/OAuthExamples.java`

## Dracoon SDK Client

`DracoonClient` is the entry point of the Dracoon SDK. It contains several handlers which group the
functions of the SDK.

__The following handlers are available:__

- Server: Query server information
- Account: Query user/customer account information, set/delete encryption key pair, ...
- Users: Not implemented yet
- Groups: Not implemented yet
- Nodes: Query node(s), create room/folder, update room/folder/file, upload/download files, ...
- Shares: Create upload and download shares

Here's an example to retrieve the user's account information:

```java
UserAccount userAccount = client.account().getUserAccount();

System.out.println("User: id=" + userAccount.getId() + ", " +
        "gender=" + userAccount.getGender() + ", " +
        "first name=" + userAccount.getFirstName() + ", " +
        "last name=" + userAccount.getLastName() + ", " +
        "email=" + userAccount.getEmail());
```

__Creating a new instance of DracoonClient:__

New client instances can be created via `DracoonClient.Builder`.

```java
DracoonAuth auth = new DracoonAuth(ACCESS_TOKEN);

DracoonClient client = new DracoonClient.Builder(new URL("https://dracoon.team"))
        .log(new Logger(Log.DEBUG))
        .auth(auth)
        .encryptionPassword(ENCRYPTION_PASSWORD)
        .build();
```

## Request and Response Models

All methods which create, modify or delete data on Dracoon use own request models. Each request
class has an builder, which you must use to create a new request.

Example to create a new root room:

```java
List<Long> adminIds = new ArrayList<>();
adminIds.add(1L);

CreateRoomRequest request = new CreateRoomRequest.Builder("Test-Room")
        .notes("This is a test room.")
        .adminUserIds(adminIds)
        .build();

Node node = client.nodes().createRoom(request);
```

The response model is always a simple data container with no further logic.

## Error Handling

The classes and methods of the Dracoon SDK use checked exceptions to indicate errors.
`DracoonException` is the base class for all exceptions. You can use it to catch all exception and
handle them separately.

__The following specific exception classes exists:__

- DracoonFileIOException: Indicates file system errors
- DracoonNetIOException: Indicates network communication errors
- DracoonApiException: Indicates Dracoon API errors
- DracoonCryptoException: Indicates encryption/decryption errors

If an error was due to a file system or network error, `DracoonFileIOException` and
`DracoonNetIOException` contain the underlying IOException which you can use for detail analysis.

If an error was due to an API or encryption/decryption error, `DracoonApiException` and
`DracoonCryptoException` contain an error code which you can use to determine the cause. Each error
code contains an unique ID which you can use to map localized error texts.

Example how to work with the two types of exceptions described above:

```java
long nodeId = 1L;

try {
    client.nodes().getNode(nodeId);
} catch (DracoonNetIOException e) {
    System.err.println(String.format("The network communication failed. (%s)", e.getCause()));
} catch (DracoonApiException e) {
    System.err.println(String.format("Query of node '%d' failed: '%s'!", nodeId,
            e.getCode().getText()));
}
```

__Request Validation:__

Before a request is sent to the server, some basic validation is done. If one or more request fields
are filled incorrectly, an `IllegalArgumentException` will be thrown. 

Mandatory fields and valid field values are documented in the JavaDoc of each request builder class.

