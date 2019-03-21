package com.jponline.wsonline.controller;

import com.jponline.wsonline.service.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ClientController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileStorage fileStorageService;

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");

        if (username == null || username.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("title", "chat room");
        return "chat";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage(Model model) {
        model.addAttribute("title", "login");
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username) {
        username = username.trim();

        if (username.isEmpty()) {
            return "login";
        }
        request.getSession().setAttribute("username", username);

        return "redirect:/";
    }

    @RequestMapping(path = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession(true).invalidate();

        return "redirect:/login";
    }


    @RequestMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(@RequestParam("pathFile") String pathFile, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadFile(pathFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}