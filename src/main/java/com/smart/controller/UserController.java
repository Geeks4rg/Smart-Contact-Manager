package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Messege;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String userName = principal.getName();
		System.out.println("UserName :- " + userName);

		User user = userRepository.getUserByItsName(userName);
		// System.out.println("User :- " + user);
		m.addAttribute("user", user);

	}

	//
	@GetMapping("/index")
	public String dashBoard(Model model, Principal principal) {

		String userName = principal.getName();
		// System.out.println("UserName :- " + userName);

		User user = userRepository.getUserByItsName(userName);
		// System.out.println("User :- " + user);
		model.addAttribute("user", user);

		model.addAttribute("tittle", user.getName());
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddcontactform(Model model) {

		model.addAttribute("tittle", " Add contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session, Model model) {

		model.addAttribute("tittle", " Add contact");

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByItsName(name);

//		 Processing profile pictures for contact
			if (file.isEmpty()) {
				// we add here a massage to be send to the client side that is FILE IS EMPTY or
				// anything like that
				contact.setImage("blank_contact.png");
				session.setAttribute("message",
						new Messege("You have added contact without profile picture", "warning"));
			}

			else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/images/conimg").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContact().add(contact);
			this.userRepository.save(user);

			System.out.println("Data....." + contact);
			System.out.println("Contact Added to Database");

			if (!file.isEmpty()) {
				session.setAttribute("message", new Messege("Contact Added successfully", "success"));
			}
		} catch (Exception e) {

			System.out.println("Exception Error occures" + e.getMessage());
			session.setAttribute("message", new Messege("Something Went Wrong please try again", "danger"));

		}

		return "normal/add_contact_form";

	}

	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal, HttpSession session) {

		m.addAttribute("tittle", "View Contacts");

		String username = principal.getName();

		User user = this.userRepository.getUserByItsName(username);
		page = page - 1;
		if (page <= 0) {
			page = 0;
		}

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contact = this.contactRepository.findContactsByUser(user.getId(), pageable);

		page = page + 1;
		m.addAttribute("contact", contact);
		m.addAttribute("currentpage", page);
		m.addAttribute("tottalpage", contact.getTotalPages());

		return "normal/showcontacts";
	}

	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		Optional<Contact> contactoptional = this.contactRepository.findById(cid);
		Contact contact = contactoptional.get();
		model.addAttribute("tittle", "View - " + contact.getName());

		String username = principal.getName();

		User user = this.userRepository.getUserByItsName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	@GetMapping("/{cid}/delete/contact")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, Principal principal,
			HttpSession session) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String username = principal.getName();

		User user = this.userRepository.getUserByItsName(username);

		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
		}

		session.setAttribute("message", new Messege("Contact Deleted successfully", "success"));

		return "redirect:/user/show-contacts/1";

	}

	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model m) {

		m.addAttribute("tittle", "Update");

		Contact contact = this.contactRepository.findById(cid).get();

		m.addAttribute("contact", contact);
		return "normal/updateform";
	}

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {

		try {

			Contact oldcontact = this.contactRepository.findById(contact.getCid()).get();

			
			  if (file.isEmpty()) {
			  
			  contact.setImage(oldcontact.getImage());
			  
			  }else {
			 
			 System.out.println("file is empty");
			  }

			User user = this.userRepository.getUserByItsName(principal.getName());

			contact.setUser(user);

			System.out.println(user);

			this.contactRepository.save(contact);

			System.out.println("this is contact :-" + contact.getName());

		} catch (Exception e) {
			System.out.println(e);
		}

		return "redirect:/user/show-contacts/1";
	}
	
	@GetMapping("/profile")
	public String userProfile(Model model,Principal principal) {
		
		String userName = principal.getName();
		// System.out.println("UserName :- " + userName);

		User user = userRepository.getUserByItsName(userName);
		// System.out.println("User :- " + user);
		model.addAttribute("user", user);
		
		model.addAttribute("tittle", "Profile - "+user.getName());
		
		
		return "normal/profile";
	}

}
