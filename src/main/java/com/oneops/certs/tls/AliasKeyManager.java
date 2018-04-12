package com.oneops.certs.tls;

import static java.util.Objects.requireNonNull;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

/**
 * A {@link X509KeyManager} implementation which selects the client private key for client
 * authentication based on given key alias.
 */
public class AliasKeyManager implements X509KeyManager {

  /** Delegate key manager */
  private final X509KeyManager delegate;

  /** Key alias name to be chosen. */
  private final String aliasName;

  public AliasKeyManager(X509KeyManager keyManager, String alias) {
    this.delegate = requireNonNull(keyManager, "KeyManager can't be null");
    this.aliasName = requireNonNull(alias, "Alias can't be null.");
  }

  /**
   * Get the matching aliases for authenticating the client side of a secure socket given the public
   * key type and the list of certificate issuer authorities recognized by the peer (if any).
   *
   * @param keyType the key algorithm type name
   * @param issuers the list of acceptable CA issuer subject names, or null if it does not matter
   *     which issuers are used.
   * @return an array of the matching alias names, or null if there were no matches.
   */
  public String[] getClientAliases(String keyType, Principal[] issuers) {
    return delegate.getClientAliases(keyType, issuers);
  }

  /**
   * Choose an alias to authenticate the client side of a secure socket given the public key type
   * and the list of certificate issuer authorities recognized by the peer (if any). Always choose
   * the selected key alias here.
   *
   * @param keyType the key algorithm type name(s), ordered with the most-preferred key type first.
   * @param issuers the list of acceptable CA issuer subject names or null if it does not matter
   *     which issuers are used.
   * @param socket the socket to be used for this connection. This parameter can be null, which
   *     indicates that implementations are free to select an alias applicable to any socket.
   * @return the alias name for the desired key, or null if there are no matches.
   */
  public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
    return aliasName;
  }

  /**
   * Get the matching aliases for authenticating the server side of a secure socket given the public
   * key type and the list of certificate issuer authorities recognized by the peer (if any).
   *
   * @param keyType the key algorithm type name
   * @param issuers the list of acceptable CA issuer subject names or null if it does not matter
   *     which issuers are used.
   * @return an array of the matching alias names, or null if there were no matches.
   */
  public String[] getServerAliases(String keyType, Principal[] issuers) {
    return delegate.getServerAliases(keyType, issuers);
  }

  /**
   * Choose an alias to authenticate the server side of a secure socket given the public key type
   * and the list of certificate issuer authorities recognized by the peer (if any).
   *
   * @param keyType the key algorithm type name.
   * @param issuers the list of acceptable CA issuer subject names or null if it does not matter
   *     which issuers are used.
   * @param socket the socket to be used for this connection. This parameter can be null, which
   *     indicates that implementations are free to select an alias applicable to any socket.
   * @return the alias name for the desired key, or null if there are no matches.
   */
  public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
    return delegate.chooseServerAlias(keyType, issuers, socket);
  }

  /**
   * Returns the certificate chain associated with the given alias.
   *
   * @param alias the alias name
   * @return the certificate chain (ordered with the user's certificate first and the root
   *     certificate authority last), or null if the alias can't be found.
   */
  public X509Certificate[] getCertificateChain(String alias) {
    return delegate.getCertificateChain(alias);
  }

  /**
   * Returns the key associated with the given alias.
   *
   * @param alias the alias name
   * @return the requested key, or null if the alias can't be found.
   */
  public PrivateKey getPrivateKey(String alias) {
    return delegate.getPrivateKey(alias);
  }
}
