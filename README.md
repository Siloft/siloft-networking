# A Java library for networking

- [Help us improve](#help_us_improve)
- [Overview](#overview)
  - [Requirements](#requirements)
  - [Generate key stores](#generate_key_stores)
- [Maven install](#maven_install)
- [Planned improvements](#planned_improvements)
- [License](#license)

## <a name='help_us_improve'>Help us improve</a>

This Networking library was developed by Siloft (https://siloft.com/). We hope users of this library are willing to help us maintain, improve, and share this library to satisfy all the needs of people around the world.

## <a name='overview'>Overview</a>

The Siloft Networking library is an easy-to-use Java library which handles reading and writing to and from TCP sockets. Each packet transmitted or received on a TCP connection is individually addressed and routed. TCP provides reliable, ordered, and error-checked delivery of a stream of octets (bytes) between applications running on hosts communicating by an IP network. Packet delivery is guaranteed.

The library has the following features:
  - An TCP server and TCP client.
  - An SSL server and SSL client which can be used for SSL and TLS communication over TCP.
  - Protocol support for easily defining, encoding, and decoding messages.

### <a name='requirements'>Requirements</a>

The following requirements are attached to this library:
  - Java 8 (or higher), standard (SE)
  - JavaFX 2.1 (or higher)

### <a name='generate_key_stores'>Generate key stores</a>

The SSL server and SSL client requires key stores matching each other in order to encrypt data before transmission. To generate these key stores the Java keytool can be used. Java keytool stores the keys and certificates in a key store, protected by a key store password. Further, it protects the private key again with another password. A Java key store contains private-public key pair and multiple trusted certificate entries. All entries in a key store are referred by aliases. Both, the private key and self signed public key, is referred by one alias while any other trusted certificates are referred by different individual aliases.

As the first step, let's create a key store for server. In order to do it, execute following command in a terminal. "server" in the following command corresponds to the private key/self signed public key certificate alias in the key store while "server.jks" is the name of the creating key store file.

```bash
keytool -genkey -alias server -keyalg RSA -keystore server.jks
```

Once you successfully completed this, Java keytool will create a file named "server.jks". In the same way, you can create a client key store named "client.jks" with the alias "client" using following command.

```bash
keytool -genkey -alias client -keyalg RSA -keystore client.jks
```

Now, you have two files named "client.jks" and "server.jks". You can view the content of these key store files using the following command. Replace "123456" with the key store password you entered while creating the key store.

```bash
keytool -list -v -keystore server.jks -storepass 123456
```

The next step is, getting server's self signed public key certificate and storing it in client's key store. And getting and storing client's self signed public key certificate in server's key store. In order to do that, first we need to export both server and client public key certificates into files. Using the following command, you can export server's public key certificate into "server.cert" file and client's public key certificate into "client.cert" file.

```bash
keytool -export -file server.cert -keystore server.jks -storepass 123456 -alias server
keytool -export -file client.cert -keystore client.jks -storepass 123456 -alias client
```

Now you have "server.cert" and "client.cert". You can use following commands to view certificate contents.

```bash
keytool -printcert -v -file server.cert
keytool -printcert -v -file client.cert
```

As the last step, we need to import "server.cert" into client key store and "client.cert" into server key store. As I mentioned earlier, each entry of a Java key store is stored against an alias. So, we need to specify aliases here, which will be used to refer the certificates that we are going to store.

```bash
keytool -import -file client.cert -keystore server.jks -storepass 123456 -alias client
```

Above command will store client's self signed public key certificate (client.cert) in "server.jks" against the alias "client". So, using "client" alias on "server.jks", we can refer client's certificate anytime. Likewise, following command will store "server.cert" within "client.jks" against the alias "server".

```bash
keytool -import -file server.cert -keystore client.jks -storepass 123456 -alias server
```

After all, please view the content of both key store again using following commands.

```bash
keytool -list -v -keystore server.jks -storepass 123456
keytool -list -v -keystore client.jks -storepass 123456
```

## <a name='maven_install'>Maven install</a>

The Siloft networking library for Java is easy to install, and you can download the binary directly from the [Downloads page](https://siloft.com/), or you can use Maven.
To use Maven, add the following lines to your pom.xml file:

```maven
<project>
  <dependencies>
    <dependency>
      <groupId>com.siloft</groupId>
      <artifactId>siloft-networking</artifactId>
      <version>0.8.5</version>
    </dependency>
  </dependencies>
</project> 
``` 

## <a name='planned_improvements'>Planned improvements</a>

The list below indicates which future improvements are planned. This does not mean they will be implemented.

1. Drop client connections which did not finished the SSL Handshake in time.
2. Improve error handling to give better indications of failures.

## <a name='license'>License</a>

[Siloft Networking library](https://siloft.com/) is open-source and licensed under the [MIT License](./LICENSE.md).
