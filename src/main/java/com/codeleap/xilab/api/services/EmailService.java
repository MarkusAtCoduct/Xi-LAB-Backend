package com.codeleap.xilab.api.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.codeleap.xilab.api.constants.EmailTemplates;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	@Value("${xilab.app.sesApiKey}")
	private String sesApiKey;

	@Value("${xilab.app.sesFromEmail}")
	private String sesFromEmail;

	@Autowired
	SpringTemplateEngine templateEngine;

	public void sendResetPasswordEmail(String firstName, String toEmail, String resetLink, int hours) throws Exception {
		String subject = "Forget Password";
		Map<String, Object> bodyData = new HashMap<>();
		bodyData.put("name", firstName);
		bodyData.put("resetLink", resetLink);
		bodyData.put("validHours", hours > 1 ? (hours + " hours.") : (hours + " hour."));
		sendEmailByTemplate(EmailTemplates.RESET_PASSWORD, subject, toEmail, bodyData);
	}


	public boolean sendEmailByTemplate(String template, String subject, String toEmail, Map<String, Object> bodyData)
			throws Exception {
		Context ctx = new Context();
		ctx.setVariable("data", bodyData);
		String bodyContent = templateEngine.process(template, ctx);

		Email from = new Email(sesFromEmail);
		Email to = new Email(toEmail);
		Content content = new Content("text/plain", bodyContent);
		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid(sesApiKey);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			if (response.getStatusCode() > 200 && response.getStatusCode() < 300) {
				log.info("Email sent to {} with message id", toEmail, response.getHeaders().get("X-Message-Id"));
			}
			return true;
		}
		catch (Exception ex) {
			throw ex;
		}
	}

}
