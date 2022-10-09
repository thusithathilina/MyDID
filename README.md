# MyDID
This provides a basic implementation of the W3C DID specification which was approved as a recommendation in late June 2022
Specification can be found on https://www.w3.org/TR/did-core/

## DID format
MyDID implementation adopts the following format when generating DIDs
```html
"did:ttd:"(namespace):identifier
namespace: idchar*
identifier: base58char+
idchar: ALPHA / DIGIT
base58char: 123456789ABCDEFGH JKLMN PQRSTUVWXYZabcdefghijk mnopqrstuvwxyz
```

e.g.
```html
did:ttd:zkjhdj365dqbbtc28hic
did:ttd:sg:zkjhdj365dqbbtc28hic
```
