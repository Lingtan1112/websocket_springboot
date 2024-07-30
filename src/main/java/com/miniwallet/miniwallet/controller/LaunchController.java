package com.miniwallet.miniwallet.controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@Controller
public class LaunchController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    
    @GetMapping("/launch")
    public String auto() {
        return "index.html";
    }


    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public String sendMessage(String message){
        System.out.println("Message box");
        return message;
    }

    public String getProgress(){
        return null;
    }


    @GetMapping("/generate/{username}/{generate}")
    @ResponseBody
    public String generate(@PathVariable("generate") int count, HttpServletRequest httpRequest) {
        CompletableFuture.runAsync(()->{
            File file = new File("Sample.txt");
            try (FileWriter fw = new FileWriter(file)) {
                for(int i=0; i<=count; i++){
                    if(i==499){
                        System.out.println("Stop");
                    }
                    int progress = (i * 100)/count;
                    if(progress == 100){
                        simpMessagingTemplate.convertAndSend("/topic/progress", "Successfully generated");
                    }else{
                        simpMessagingTemplate.convertAndSend("/topic/progress", ""+progress+" %");
                    }
                   
                    fw.write("New Line ::: " + i);
                    System.out.println("New Line ::: " + i);
                    Thread.sleep(5);
                    fw.flush();
                }
            } catch (IOException | InterruptedException e ) {
                System.out.println("Error 1 ::: " + e.getMessage());
                e.printStackTrace();
            } catch(Exception ex){
                System.out.println("Error 2 ::: " + ex.getMessage());
                ex.printStackTrace();
            }
            
        });
        simpMessagingTemplate.convertAndSend("/topic/progress", "Generation Started");
        return "Generation started successfully";
    }

    @GetMapping("/login/{username}")
    @ResponseBody
    public String login(@PathVariable("username") String username, HttpServletRequest httpRequest) {
        httpRequest.getSession().setAttribute("username", username);
        return "Logged in successfully";
    }

}
