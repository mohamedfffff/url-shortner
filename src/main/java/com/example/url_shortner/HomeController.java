package com.example.url_shortner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@Controller
public class HomeController {
	
	@GetMapping("/")
	public String HomePage(){
		return "index.html";
	}
	
}
