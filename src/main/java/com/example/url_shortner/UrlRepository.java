package com.example.url_shortner;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UrlRepository extends  JpaRepository<UrlModel, Long> {
	
	UrlModel findByShortKey(String shortKey);

}
