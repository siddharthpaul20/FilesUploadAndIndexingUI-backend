package io.springbootstarter.WebSocketController;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import io.springbootstarter.ControllerClass.ControllerClass;
import io.springbootstarter.CountFilesGettingIndexed.CountFilesGettingIndexed;

import java.util.concurrent.atomic.AtomicLong;

@Controller
public class WebSocketController {

    public static final String SENDING_URL = "/topic/server-broadcaster";
    public static final String RECEIVING_URL = "/server-receiver";
    
    static final Logger logger = Logger.getLogger(WebSocketController.class);

    public static SimpMessagingTemplate template;
    private AtomicLong counter = new AtomicLong(0);

    private String message = "";

    @Autowired
    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping(RECEIVING_URL)
    public void onReceivedMessage(String message) {
        System.out.println("New message received : " + message);
    }

    @SubscribeMapping(SENDING_URL)
    public String onSubscribe() {
        return "Successfully connected with backend server : " + message;
    }

	/*
	 * @Scheduled(fixedRate = 1000) public void sendMessage() {
	 * template.convertAndSend(SENDING_URL, buildNextMessage()); }
	 */

    public static String buildNextMessage() {
        //message = "Test" + counter.getAndIncrement();
    	float totalFilesSubmitted = CountFilesGettingIndexed.getTotalFilesSubmitted();
    	float totalFilesIndexed = CountFilesGettingIndexed.getTotalFilesIndexed();
    	String message = "Progress : TotalFilesSubmittedForIndexing = "+totalFilesSubmitted+"|| TotalFilesIndexed = "+totalFilesIndexed+"|| Progress = "+(totalFilesIndexed/totalFilesSubmitted)*100;
        //float value = (totalFilesIndexed/totalFilesSubmitted)*100;
        //message = Float.toString(value);
    	logger.info("Send message " + message);
        return message;
    }
}

