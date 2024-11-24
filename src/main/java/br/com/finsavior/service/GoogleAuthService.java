package br.com.finsavior.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public interface GoogleAuthService {
    GoogleIdToken.Payload validateGoogleToken(String idTokenString);
}
