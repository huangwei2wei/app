package com.wyd.combined.bean;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * The persistent class for the tab_empireaccount database table.
 * 
 * @author BEA Workshop
 */
@Entity()
@Table(name = "tab_empireaccount")
public class Empireaccount implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long        serialVersionUID = 1L;
    private Integer                  id;
    private Integer                  accountId;
    private String                   serverid;

    public Empireaccount() {
    }

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, precision = 10)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @Basic()
    @Column(name = "serverid", length = 255)
    public String getServerid() {
        return this.serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }
    
    @Basic()
    @Column(name = "accountId", precision = 10)
    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}