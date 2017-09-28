/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset.cordovahttp;

import java.net.UnknownHostException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.cordova.CallbackContext;

import org.json.JSONException;
import org.json.JSONObject;

import org.asynchttpclient.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

class CordovaHttpGet extends CordovaHttp implements Runnable {
    public CordovaHttpGet(String urlString, JSONObject params, JSONObject headers, CallbackContext callbackContext) {
        super(urlString, params, headers, callbackContext);
    }

    @Override
    public void run() {
        try {
            // HttpRequest request = HttpRequest.get(this.getUrlString(), this.getParamsMap(), false);

            // this.setupSecurity(request);
            // this.setupTimeouts(request);
            // request.acceptCharset(CHARSET);
            // request.headers(this.getHeadersMap());

            int code = 200;//request.code();
            String body;// = request.body(CHARSET);
            JSONObject response = new JSONObject();

            AsyncHttpClient c = new DefaultAsyncHttpClient(); // cf
            Future<Response> f = c.prepareGet("https://104.16.0.0/")
                    .setHeader("Host", "www.bitnrg.io")
                    .setVirtualHost("www.bitnrg.io")
                    .execute();
            Response r = f.get();
            body = r.getResponseBody();

            // this.addResponseHeaders(request, response);
            response.put("status", code);

            if (code >= 200 && code < 300) {
                response.put("data", body);
                this.getCallbackContext().success(response);
            } else {
                response.put("error", body);
                this.getCallbackContext().error(response);
            }
        } catch (JSONException e) {
            this.respondWithError("There was an error generating the response");
        } catch (InterruptedException e) {
            this.respondWithError("InterruptedException");
        }catch (ExecutionException e) {
            this.respondWithError("ExecutionException");
        }catch (HttpRequestException e) {
            if (e.getCause() instanceof UnknownHostException) {
                this.respondWithError(0, "The host could not be resolved");
            } else if (e.getCause() instanceof SSLHandshakeException) {
                this.respondWithError("SSL handshake failed oh yeah");
            } else if (e.getCause() instanceof SocketTimeoutException) {
                this.respondWithError("Timeout");
            }else {
                this.respondWithError("There was an error with the request");
            }
        }
    }
}
