package org.baeldung.web.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

/**
 * this class is used to populate the content of the mail template 
 * @author Halim zaaim
 *
 */
@PropertySource("classpath:application.properties") 
public class MailContent {
	private String url;
	private String subject;
	private String logo;
	private String action;
	private List<String> bodyContent;
	private List<String> footerContent;
    @Value("${mail.logo.classpath}")
    private String logoClassPth;
	private List<File> fileAttachments = new ArrayList<>();
	
	public MailContent(String url, String subject,String action, String logoName) {
		super();
		this.url = url;
		this.subject = subject;
		this.action = action;
		// this.setLogo(logoName);
		this.logo = logoName;
		this.bodyContent = new ArrayList<String>();
		this.footerContent = new ArrayList<String>();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getLogo() {
		return logo;
	}
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

    /**
     * @param logo
     * set the logo URL for the email template
     * @throws FileNotFoundException 
     */
    public void setLogo(String logoName) throws FileNotFoundException {
        // Remove leading slash if present
        String normalizedPath = logoName.startsWith("/") ? logoName.substring(1) : logoName;
        
        // Build the URL path relative to your application's static resources
        this.logo = normalizedPath;
    }

	public List<String> getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(List<String> bodyContent) {
		this.bodyContent = bodyContent;
	}

	public List<String> getFooterContent() {
		return footerContent;
	}

	
	public void setFooterContent(List<String> footerContent) {
		this.footerContent = footerContent;
	}
	/**
	 * @param footerContent
	 * this method take an array of String then build a List 
	 * @return void
	 */	
	public void buildListOfBodyContent(String ...bodyMessages)
	{
		
		for (int i = 0; i < bodyMessages.length; i++) {
			this.bodyContent.add(bodyMessages[i]);
		}
	}
	
	/**
	 * @param footerMessages
	 * this method take an array of String then build a List 
	 * @return void
	 */
	public void buildListOfFooterContent(String ...footerMessages)
	{
		for (int i = 0; i < footerMessages.length; i++) {
			this.footerContent.add(footerMessages[i]);
		}
	}
	
	public List<File> getFileAttachments() {
		return fileAttachments;
	}

	public void setFileAttachments(List<File> fileAttachments) {
		this.fileAttachments = fileAttachments;
	}
    public List<File> addFileAttachment(File file)
    {
    	fileAttachments.add(file);
    	return this.fileAttachments;
    }
    public boolean isEmptyFileAttachments()
    {
    	return fileAttachments.isEmpty();
    }
	public void defaultFootter()
	{
		String[] footerMessages = {"شارع مولاي يوسف - الرباط | الهاتف : +212 537 72 43 96","فاكس : +212 5 37 20 34 10"};
		this.buildListOfFooterContent(footerMessages);
	}
	public void defaultLogo()
	{
		this.logo = "https://lh3.googleusercontent.com/pw/AP1GczPSx3-zK4FTovQnA0O280NGfHgxxUOaOOUDqLlGgIPDDh5cE5hkLcm1o-u_0HDdDjTfU6SSNn7grXRVFBPkt5kn6eBe5kE-b-dow7qqff4FBFakuFD_OXdiZUu-5jCpLi07FA9ErgPglFOGM205BhpU=w836-h300-s-no?authuser=0";
	}
	
}
