package {service.namespace}.utils.messages;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
{customcode.import}

public class MessageManager {

	private Messages logs = new Messages();
	private MessageType highestMessageType = MessageType.Information;
	private Map<String, Messages> logsByGroup = new TreeMap<>();
	private Map<MessageType, Messages> logsByType = new TreeMap<>();

	{customcode.start}

	public MessageManager() {
		super();
	}

	/**
	 * Create new Message Manager
	 */
	public static MessageManager newInstance() {
		return new MessageManager();
	}
	
	/**
	 * Add all Messages from another MessageManager
	 * 
	 * @param messageManager
	 */
	public void add(MessageManager messageManager) {
		for (Message message : messageManager.getMessages()) {
			add(message);
		}
	}

	/**
	 * Add Info Message to the List of Messages
	 * 
	 * @param group
	 * @param title
	 * @param description
	 * @return message info
	 */
	public Message info(String group, String title, String description) {
		return add(MessageType.Information, group, title, description, null, null);
	}

	/**
	 * Add Warning Message to the List of Messages
	 * 
	 * @param group
	 * @param title
	 * @param description
	 * @return message info
	 */
	public Message warning(String group, String title, String description) {
		return add(MessageType.Information, group, title, description, null, null);
	}

	/**
	 * Add Error Message to the List of Messages
	 * 
	 * @param group
	 * @param title
	 * @param description
	 */
	public Message error(String group, String title, String description) {
		return add(MessageType.Error, group, title, description, null, null);
	}

	/**
	 * Add Message to the List of Messages
	 * 
	 * @param type
	 * @param group
	 * @param title
	 * @param description
	 */
	public Message add(MessageType type, String group, String title, String description) {
		return add(type, group, title, description, null, null);
	}

	/**
	 * Add Message to the List of Messages
	 * 
	 * @param type
	 * @param group
	 * @param title
	 * @param description
	 * @param subtitle
	 */
	public Message add(MessageType type, String group, String title, String description, String subtitle) {
		return add(type, group, title, description, subtitle, null);
	}

	/**
	 * Add Message to the List of Messages
	 * 
	 * @param type
	 * @param group
	 * @param title
	 * @param description
	 * @param subtitle
	 * @param link
	 */
	public Message add(MessageType type, String group, String title, String description, String subtitle, String link) {
		return add (new Message(type, LocalDateTime.now(), group, title, description, subtitle, link));
	}

	/**
	 * Add Message 
	 * 
	 * @param message
	 */
	public Message add(Message message) {
		if (message.getType().ordinal() > highestMessageType.ordinal()) {
			highestMessageType = message.getType();
		}
		
		// STANDARD LOG
		logs.add(message);

		// LOG BY CATEGORY
		if (!logsByGroup.containsKey(message.getGroup())) {
			logsByGroup.put(message.getGroup(), new Messages());
		}
        logsByGroup.get(message.getGroup()).add(message);

		// LOG BY MessageType
		if (!logsByType.containsKey(message.getType())) {
			logsByType.put(message.getType(), new Messages());
		}
		logsByType.get(message.getType()).add(message);
		
		return message;
	}
	
	/**
	 * get Logs in Insertion Order
	 */
	public Messages getMessages() {
		return logs;
	}

	/**
	 * Get overall State of all entries. if at least one is error, the state is error, analog for
	 * warning,default is success if no actions are available
	 * 
	 * @return
	 */
	public MessageType getHighestMessageType() {
		return highestMessageType;
	}

	/**
	 * Get Logs by Category
	 */
	public Map<String, Messages> getMessagesByGroup() {
		return logsByGroup;
	}

	/**
	 * Get Logs by given group/category
	 * 
	 * @param group
	 * @return ActionsLogs, never null
	 */
	public Messages getMessagesByGroup(String group) {
		if (logsByGroup.containsKey(group)) {
			return logsByGroup.get(group);
		} else {
			return new Messages();
		}
	}

	/**
	 * Get Logs by State
	 * 
	 * @return
	 */
	public Map<String, Messages> getMessagesByType() {
		return getMessagesByType();
	}

	/**
	 * Get Logs by given Message Type
	 * 
	 * @return ActionsLoglogsByMessageTypell
	 */
	public Messages getMessagesByType(MessageType type) {
		if (logsByType.containsKey(type)) {
			return logsByType.get(type);
		} else {
			return new Messages();
		}
	}
	
	{customcode.end}

}
