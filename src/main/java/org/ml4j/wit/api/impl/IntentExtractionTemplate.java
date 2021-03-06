/*
 s* Copyright 201 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ml4j.wit.api.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.ml4j.wit.api.IntentExtractionOperations;
import org.ml4j.wit.api.impl.json.Context;
import org.ml4j.wit.api.impl.json.IntentExtractionResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Michael Lavelle
 */
public class IntentExtractionTemplate extends AbstractWitResourceOperations implements IntentExtractionOperations {

	public IntentExtractionTemplate(String oauthApiBaseUrl, RestTemplate restTemplate, String version) {
		super(oauthApiBaseUrl, restTemplate, version);
	}

	@Override
	protected String getApiResourceBaseUrl() {
		return getApiBaseUrl();
	}

	@Override
	public IntentExtractionResponse getIntent(String message) {
		return restTemplate.getForObject(getApiResourceUrl("/message?q=" + message), IntentExtractionResponse.class);
	}
	
	@Override
	public IntentExtractionResponse getIntent(String message,Context context) {
		try {
			return restTemplate.getForObject(new URI(getApiResourceUrl("/message?q=" + URLEncoder.encode(message,"UTF-8") + "&context=" + context.toEncodedParameterValue())), IntentExtractionResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IntentExtractionResponse getIntent(File audioFile) throws IOException {

		final FileSystemResource resource = new FileSystemResource(audioFile);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(resource.contentLength());
		headers.setContentType(new MediaType("audio", "wav"));
		HttpEntity<Resource> request = new HttpEntity<Resource>(resource, headers);
		return restTemplate.postForObject(getApiResourceUrl("/speech"), request, IntentExtractionResponse.class);
	}
	
	@Override
	public IntentExtractionResponse getIntent(File audioFile,Context context) throws IOException {

		final FileSystemResource resource = new FileSystemResource(audioFile);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(resource.contentLength());
		headers.setContentType(new MediaType("audio", "wav"));
		HttpEntity<Resource> request = new HttpEntity<Resource>(resource, headers);
		try {
			return restTemplate.postForObject(new URI(getApiResourceUrl("/speech?context=" +  context.toEncodedParameterValue())), request, IntentExtractionResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IntentExtractionResponse getPreviousIntentById(String id) {
		return restTemplate.getForObject(getApiResourceUrl("/messages/" + id), IntentExtractionResponse.class);
	}

}
