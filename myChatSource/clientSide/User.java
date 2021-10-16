package clientSide;

public class User {

	private String username;
	private Boolean status;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setStatus(String status) {
		if (status.equals("online"))
			this.status = true;
		else
			this.status = false;
	}

	public String getUsername() {
		return username;
	}

	public Boolean getStatus() {
		return status;
	}
}
