package com.petadoption.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuration class for JSON Web Token (JWT) handling using RSA public and private keys.
 * This class is responsible for initializing RSA keys, as well as creating beans for
 * JWT encoding and decoding.
 */
@Configuration
public class JwtConfig {

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    /**
     * Loads RSA keys from files in resources/keys when initializing the class.
     */
    @PostConstruct
    public void initKeys() {
        try {
            // Read private key from resources
            ClassPathResource privateKeyResource = new ClassPathResource("keys/private_key_base64.pem");
            String privateKeyContent = Files.readString(privateKeyResource.getFile().toPath(), StandardCharsets.UTF_8);
            privateKeyContent = privateKeyContent.trim();

            // Read public key from resources
            ClassPathResource publicKeyResource = new ClassPathResource("keys/public_key_base64.pem");
            String publicKeyContent = Files.readString(publicKeyResource.getFile().toPath(), StandardCharsets.UTF_8);
            publicKeyContent = publicKeyContent.trim();

            // Decode Base64 keys to RSA objects
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
            );
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent))
            );

            logger.info("RSA keys successfully loaded from resources/keys");

        } catch (IOException e) {
            logger.error("Error reading RSA key files from resources directory", e);
            throw new IllegalStateException("RSA keys could not be loaded", e);
        } catch (Exception e) {
            logger.error("Error processing RSA keys", e);
            throw new IllegalStateException("Could not initialize RSA keys", e);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        logger.info("Initializing the JWT Encoder with RSA keys...");

        // Build RSAKey based on read keys
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("rsa-key")
                .build();

        // Create a JWKSet and initialize the encoder (JwtEncoder)
        JWKSet jwkSet = new JWKSet(rsaKey);
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        return new NimbusJwtEncoder(jwkSource);
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        logger.info("Initializing the JWT Decoder with RSA keys...");

        // Create the decoder using only the public key
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
