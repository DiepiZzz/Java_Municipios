package Model.Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;


@Entity
@Table(name = "password_recovery_tokens")
public class PasswordRecoveryToken implements Serializable {

    @Id
    private String token;


    @ManyToOne

    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user;

    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Column(name = "used", nullable = false)
    private boolean used;


    public PasswordRecoveryToken() {
    }


    public PasswordRecoveryToken(String token, Usuario user, Date expiryDate, boolean used) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.used = used;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
