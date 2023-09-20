package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Messege;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/home")
	public String home(Model model) {
		System.out.println("Home page opened");
		model.addAttribute("tittle","Home - Smart Contact Manager");
		
		
		return "home";
		
		
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("tittle","About - Smart Contact Manager");
		return "about";
		
		
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		System.out.println("singup page opened");
		model.addAttribute("tittle","Register - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	
	
	@GetMapping("/signin")
	public String login(Model model) {
		
		model.addAttribute("tittle","Login-page");
		return "login";
	}
	
		
	
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,Model model,HttpSession session) {
		
		try {
			if(!agreement){
				System.out.println("please accept the terms & condition for signup");
				throw new Exception("Please accept the terms & condition for signup");
			}
			
			if(result.hasErrors()){
				System.out.println("Error"+result.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			try {
				User saveresult = this.userRepository.save(user);
			}catch (Exception e) {
			
				e.printStackTrace();
				model.addAttribute("user", user);
				session.setAttribute("message", new Messege("This Email id is already registerd try to login or register with new email id","alert-danger"));
				return "signup";
			}
			
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Messege("Successfully registerd !!","alert-success"));
			
			
			return "signup";
		
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Messege("Error!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
	}
	
	
	
}
	

