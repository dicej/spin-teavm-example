# spin-teavm-example

This is a example demonstrating how to use [TeaVM-WASI](https://github.com/fermyon/teavm-wasi), [wit-bindgen](https://github.com/bytecodealliance/wit-bindgen), and [Spin](https://github.com/fermyon/spin) to build and run a simple stateless web service in Java, targetting WebAssembly

## Prerequisites

- [A recent JDK (e.g. OpenJDK 18)](https://jdk.java.net/18/)
- [Maven](https://maven.apache.org/download.cgi)
- [Spin 0.6.0](https://github.com/fermyon/spin/releases/tag/v0.6.0)
- [cURL](https://curl.se/download.html) (or a web browser) for testing

On Ubuntu 22.04 x86_64, this should do the job:

```sh
sudo apt install openjdk-18-jdk maven curl
curl -L -O https://github.com/fermyon/spin/releases/download/v0.6.0/spin-v0.6.0-linux-amd64.tar.gz
tar xf spin-v0.6.0-linux-amd64.tar.gz
sudo cp spin /usr/local/bin/
```

## Building, running, and testing

```sh
spin build
spin up &
curl http://localhost:3000
```

## (Optional) Re-generate bindings

### Prerequisites

- [Rust](https://rustup.rs/)
- A recent wit-bindgen (git commit 9609ca8 is known to work)

```sh
cargo install --git https://github.com/bytecodealliance/wit-bindgen \
    --rev 9609ca8daf2dadcb71082a1ad7ec86bbf0fbb5ef --locked wit-bindgen-cli
```

### (Re-)generating the bindings

```sh
wit-bindgen guest teavm-java --export wit/spin-http.wit \
    --out-dir src/main/java/wit_spin_http/
wit-bindgen guest teavm-java --import wit/wasi-outbound-http.wit \
    --out-dir src/main/java/wit_wasi_outbound_http/
```
