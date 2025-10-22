package org.baeldung.persistence.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="adressemac")
public class AdresseMac {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;
	    private Long userId;
	    private String mac;
	    private Date lastLoggedIn;
	    private int supprime;
	    
	    
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public Date getLastLoggedIn() {
			return lastLoggedIn;
		}
		public void setLastLoggedIn(Date lastLoggedIn) {
			this.lastLoggedIn = lastLoggedIn;
		}
		public int getSupprime() {
			return supprime;
		}
		public void setSupprime(int supprime) {
			this.supprime = supprime;
		}
	    
		@Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        AdresseMac that = (AdresseMac) o;
	        return Objects.equals(id, that.id) &&
	                Objects.equals(userId, that.userId) &&
	                Objects.equals(mac, that.mac) &&
	                Objects.equals(supprime, that.supprime) &&
	                Objects.equals(lastLoggedIn, that.lastLoggedIn);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id, userId, mac, supprime, lastLoggedIn);
	    }

	    @Override
	    public String toString() {
	        final StringBuilder sb = new StringBuilder("DeviceMetadata{");
	        sb.append("id=").append(id);
	        sb.append(", userId=").append(userId);
	        sb.append(", mac='").append(mac).append('\'');
	        sb.append(", supprime='").append(supprime).append('\'');
	        sb.append(", lastLoggedIn=").append(lastLoggedIn);
	        sb.append('}');
	        return sb.toString();
	    }

}
