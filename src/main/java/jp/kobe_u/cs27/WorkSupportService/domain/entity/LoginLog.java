package jp.kobe_u.cs27.WorkSupportService.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ログインログのエンティティ
 *
 * @author ing
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginLog {
    /** あるユーザが */
    @Id
    private String uid;

    /** 最終ログイン */
    private Date lastlogin;

    /** 現在の連続ログイン日数 */
    private int loginstreak;

    /** 最大連続ログイン日数 */
    private int maximumloginstreak;
}
