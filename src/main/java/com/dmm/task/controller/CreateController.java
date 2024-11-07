package com.dmm.task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CreateController {
	 @GetMapping(value = "/main/create/{date}")
	  public String create(@PathVariable("date") String dateStr) {
	    	
	        return "main/create";
	        }

}
