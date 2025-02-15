# Provenance Blockchain Protobuf Install and Build

The Provenance Explorer uses a combination of [Provenance](https://github.com/provenance-io/provenance), 
[Cosmos](https://github.com/cosmos/cosmos-sdk), [provenance-io/wasmd](https://github.com/provenance-io/wasmd) 
and [IBC](https://github.com/cosmos/ibc-go) [protobuf](https://developers.google.com/protocol-buffers) definitions.
Protocol buffers (protobuf) are Google's language-neutral, platform-neutral, 
extensible mechanism for serializing structured data.  The Provenance
[gRPC](https://grpc.io) and protobuf provide the RPC mechanism that Provenance 
Explorer (and all middleware, really) uses to communicate with the Provenance blockchain.

## Download Provenance Blockchain Protos

This `proto` module compiles the protobuf definitions (protos) from the `third_party` directory.
The compiled protos are then used in the Provenance Explorer `service` module
to communicate with the blockchain.

Before compiling the protos, they must be downloaded locally.  The `third_party`
directory contains the last download of the protos.  To update the `third_party`
directory run this `gradle` task *from the root project directory*:

```bash
./gradlew proto:downloadProtos
```

> The `downloadProtos` task will clean the `third_party` directory prior to
> download.  Do not edit the protos in that directory.

This `gradle` task will download the Provenance, Cosmos, and provenance-io/wasmd proto versions defined
in the `./buildSrc/src/main/kotlin/Dependencies.kt` file:

```kotlin
    //external protos
    const val Provenance = "v1.7.5"
    const val Cosmos = "v0.44.3"
    const val Wasmd = "v0.19.0"
    const val Ibc = "v1.1.0"
```

To manually specify the versions run this `gradle` task  *from the root project directory*:

```bash
./gradlew downloadProtos --provenance-version v1.7.5 --cosmos-version v0.44.3 --wasmd-version v0.19.0 --ibc-version v1.1.0
```

> The proto download process does not need to be run very often, 
> only when major version of the Provenance or Cosmos proto definitions
> are released.

## Build Protos

Once the protos have been downloaded, run the `gradle` task *from the root project directory*:

```bash
./gradlew clean proto:generateProto
```
