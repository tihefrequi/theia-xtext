package {batch.namespace}.base;

import javax.mail.Session;

import {service.namespace}.utils.mail.MailApi;

public class AbstractEmailTask {

    /**
     * send Email
     * 
     * @param session
     * @param from
     * @param subject
     * @param content
     * @param debug
     * @param debugToUser
     * @param toEmails
     */
    protected void sendEmail(Session session, String from, String subject, String content,
        boolean debug, String debugToUser, String... toEmails) {
		if (debug) {
		  MailApi.get().session(session).from(from).subject(subject).content(content).debug(debug).to(debugToUser);
		} else {
		  MailApi.get().session(session).from(from).subject(subject).content(content).debug(debug).to(toEmails);
		}
    }

}
