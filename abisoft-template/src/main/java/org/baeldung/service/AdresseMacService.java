package org.baeldung.service;

import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.baeldung.persistence.dao.AdresseMacRepository;
import org.baeldung.persistence.model.AdresseMac;
import org.baeldung.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.exception.GeoIp2Exception;

@Component
public class AdresseMacService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AdresseMacRepository adresseMacRepository;
	
	public AdresseMacService(AdresseMacRepository adresseMacRepository) {
		this.adresseMacRepository = adresseMacRepository;
	}

	public void verifyDevice(User user, HttpServletRequest request) throws IOException, GeoIp2Exception {

        String ip = extractIp(request);
        System.out.println("Adresse Ip: "+ip);
        String mac = getMACAddress(ip);
        System.out.println("Adresse mac: "+mac);
        //String location = getIpLocation(ip);
        AdresseMac existingMac = findExistingMac(user.getId(), mac);

        if (Objects.isNull(existingMac)) {
            AdresseMac adresseMac = new AdresseMac();
            adresseMac.setUserId(user.getId());
            adresseMac.setSupprime(0);
            adresseMac.setMac(mac);
            adresseMac.setLastLoggedIn(new Date());
            adresseMacRepository.save(adresseMac);
        } else {
        	existingMac.setLastLoggedIn(new Date());
        	adresseMacRepository.save(existingMac);
        }

    }
	
	 private AdresseMac findExistingMac(Long userId, String mac) {
	        List<AdresseMac> knownDevices = adresseMacRepository.findByUserId(userId);
	        for (AdresseMac existingMac : knownDevices) {
	            if (existingMac.getMac().equals(mac)) {
	                return existingMac;
	            }
	        }

	        return null;
	    }
	
	private String extractIp(HttpServletRequest request) {
        String clientIp;
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        if (nonNull(clientXForwardedForIp)) {
            clientIp = parseXForwardedHeader(clientXForwardedForIp);
        } else {
            clientIp = request.getRemoteAddr();
           // clientIp = request.getLocalAddr();
        }

        return clientIp;
    }
	private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    public static String getMACAddress(String ip) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return getMacAddressWindows(ip);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return getMacAddressLinux(ip);
        } else {
            return "Unsupported OS";
        }
    }

    private static String getMacAddressWindows(String ip) {
        String macAddress = "";
        try {
            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("MAC Address")) {
                    int index = line.indexOf("=");
                    macAddress = line.substring(index + 1).trim().toUpperCase();
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    private static String getMacAddressLinux(String ip) {
        String macAddress = "";
        try {
            Process p = Runtime.getRuntime().exec("arp " + ip);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(ip)) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        macAddress = parts[2];
                        break;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;
    }
}
