package com.example.url_shortner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {
	
	private final UrlRepository urlRepository;
	
	public UrlService(UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}
	
	private final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final int base = ALPHABET.length();
	
	public String shortenUrl(String longUrl) {
		UrlModel url = new UrlModel();
		url.setLongUrl(longUrl);
		
		UrlModel saved = urlRepository.save(url);
		Long id = saved.getId();
		
		String shortKey = encode(id);
		
		saved.setShortKey(shortKey);
		urlRepository.save(saved);
		return shortKey;
	}
	
	public Optional<String> retrieveLongUrl(String shortUrl) {
		UrlModel link = urlRepository.findByShortKey(shortUrl);
		if (link == null) return Optional.empty();
		return Optional.of(link.getLongUrl());
	}
	
	public String encode(Long num) {
		StringBuilder sb = new StringBuilder();
		while (num > 0) {
			sb.insert(0, ALPHABET.charAt((int) (num % base)));
			num /= base;
		}
		return sb.toString();
	}

}
