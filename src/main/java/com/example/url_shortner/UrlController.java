package com.example.url_shortner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;


@RestController
public class UrlController {
	@Autowired
	private final UrlService urlService;
	
	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

	@PostMapping("api/v1/shorten")
	public ResponseEntity<String> shortenUrl(@RequestBody String longUrl){
		if (longUrl == null) {
			return new ResponseEntity<>("URL can not be empty", HttpStatus.BAD_REQUEST);
		}
		
		try {
			String shortUrl = urlService.shortenUrl(longUrl);
			String fullShortUrl = "http://localhost:8080/api/v1/" + shortUrl;
			return new ResponseEntity<>(fullShortUrl, HttpStatus.CREATED);
		} catch(Exception e){
			System.out.println(e);
			return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		
	}
	
	@GetMapping("api/v1/{shortUrl}")
	public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl){
		Optional<String> longUrl = urlService.retrieveLongUrl(shortUrl);
		if (longUrl.isPresent()) {
			return ResponseEntity
					.status(HttpStatus.FOUND)
					.location(URI.create(longUrl.get()))
					.build();
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	

}
