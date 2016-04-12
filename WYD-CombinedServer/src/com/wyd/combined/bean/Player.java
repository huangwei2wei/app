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
 * The persistent class for the tab_player database table.
 * 
 * @author BEA Workshop
 */
@Entity()
@Table(name = "tab_player")
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer           id;
    private Integer           accountId;
    private Integer           areaId;

    public Player() {
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
    @Column(name = "accountId", precision = 10)
    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    @Basic()
    @Column(name = "areaId", precision = 10)
    public Integer getAreaId() {
        return this.areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}