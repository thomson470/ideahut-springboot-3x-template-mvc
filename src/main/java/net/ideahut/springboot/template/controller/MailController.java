package net.ideahut.springboot.template.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailObject;
import net.ideahut.springboot.mail.MailObject.Attachment;
import net.ideahut.springboot.object.Result;

/*
 * Contoh penggunaan MailHandler
 */
@Public
@ComponentScan
@RestController
@RequestMapping("/mail")
class MailController {

	private final MailHandler mailHandler;
	
	@Autowired
	MailController(
		MailHandler mailHandler	
	) {
		this.mailHandler = mailHandler;
	}
	
	@Setter
	@Getter
	static class Form {
		private String from;
		private List<String> to;
		private List<String> cc;
		private List<String> bcc;
		private String subject;
		private String content;
		private MultipartFile attachment;
	}
	
	@PostMapping("/send/sync")
	Result sendSync(@ModelAttribute Form form) throws Exception {
		sendMail(form, false);
		return Result.success();
	}
	
	@PostMapping("/send/async")
	Result sendAsync(@ModelAttribute Form form) throws Exception {
		sendMail(form, true);
		return Result.success();
	}
	
	private void sendMail(Form form, boolean async) throws Exception {
		MailObject mail = new MailObject();
		
		String subject = ObjectHelper.useOrDefault(form.getSubject(), "");
		subject = ObjectHelper.useOrElse(!subject.trim().isEmpty(), subject, "Test-Mail");
		mail.setSubject(subject);
		
		String content = ObjectHelper.useOrDefault(form.getContent(), "");
		content = ObjectHelper.useOrElse(!content.trim().isEmpty(), content, "Ini adalah contoh email");
		mail.setHtmlText(content);
		
		String sender = ObjectHelper.useOrDefault(form.getFrom(), "").trim();
		if (!sender.isEmpty()) {
			mail.setFrom(new InternetAddress(sender, sender));
		}
		
		if (form.getTo() != null) {
			List<InternetAddress> to = new ArrayList<>();
			for (String email : form.getTo()) {
				to.add(new InternetAddress(email, email));
			}
			mail.setTo(to.toArray(new InternetAddress[0]));
		}
		
		if (form.getCc() != null) {
			List<InternetAddress> cc = new ArrayList<>();
			for (String email : form.getCc()) {
				cc.add(new InternetAddress(email, email));
			}
			mail.setCc(cc.toArray(new InternetAddress[0]));
		}
		
		if (form.getBcc() != null) {
			List<InternetAddress> bcc = new ArrayList<>();
			for (String email : form.getBcc()) {
				bcc.add(new InternetAddress(email, email));
			}
			mail.setBcc(bcc.toArray(new InternetAddress[0]));
		}
		
		MultipartFile multipart = form.getAttachment();
		if (multipart != null) {
			mail.setMultipart(true);
			Attachment attachment = Attachment.of("Attachment", multipart.getBytes(), multipart.getContentType());
			mail.setAttachment(attachment);
		}
		mailHandler.send(mail, async);
	}
	
}
