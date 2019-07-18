package com.example.demo;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model) {
        model.addAttribute("message", new Message());
        //return "messageform";
        return "HowToDoMessage";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result) {
        if (result.hasErrors()){
            //return "messageform";
            return "HowToDoMessage";
        }
        messageRepository.save(message);
        return "redirect:/";
    }

    @PostMapping("/add")
    public String processMessage(@ModelAttribute Message message,
                                 @RequestParam("file")MultipartFile file){
        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
                    message.setImage(uploadResult.get("url").toString());
                    messageRepository.save(message);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }



    @RequestMapping("/detail/{id}")
    public String showMessages(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessages(@PathVariable("id") long id,  Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        //return "messageform";
        return "HowToDoMessage";
    }

    @RequestMapping("/delete/{id}")
    public String delMessages(@PathVariable("id") long id,  Model model) {
        messageRepository.deleteById(id);
        return "redirect:/";
    }

}
