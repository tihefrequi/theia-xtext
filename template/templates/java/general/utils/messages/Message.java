package {service.namespace}.utils.messages;

import java.time.LocalDateTime;
{customcode.import}

public class Message {
	private MessageType type;
	private LocalDateTime date;
	private String group;
	private String title;
	private String subTitle;
	private String description;
	private String link = null;

	{customcode.start}

	public Message(MessageType type, LocalDateTime date, String group, String title, String description) {
		this(type, date, group, title, description, null, null);
	}

	public Message(MessageType type, LocalDateTime date, String group, String title, String description,
			String subtitle) {
		this(type, date, group, title, description, subtitle, null);
	}

	public Message(MessageType type, LocalDateTime date, String group, String title, String description,
			String subTitle, String link) {
		super();
		this.type = type;
		this.date = date;
		this.group = group;
		this.title = title;
		this.subTitle = subTitle;
		this.description = description;
		this.link = link;
	}

	/**
	 * Get Message Text suitable e.g. to return for e.g. throwing runtime
	 * exceptions with this infos
	 */
	public String text() {
		return getGroup() + " - " + getTitle();
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "Message [type=" + type + ", date=" + date + ", group=" + group + ", title=" + title + ", subTitle="
				+ subTitle + ", description=" + description + ", link=" + link + "]";
	}

	{customcode.end}

}
