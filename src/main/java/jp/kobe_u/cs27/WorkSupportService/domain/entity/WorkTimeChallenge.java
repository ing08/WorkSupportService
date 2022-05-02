package jp.kobe_u.cs27.WorkSupportService.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 目標作業時間のエンティティ
 *
 * @author ing
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeChallenge {
    /** 目標作業時間ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wtcid;

    /** 誰のチャレンジ情報か */
    private String uid;

    /** いつからのチャレンジか */
    @Temporal(TemporalType.TIMESTAMP)
    private Date since;

    /** いつまでのチャレンジか */
    @Temporal(TemporalType.TIMESTAMP)
    private Date until;

    /** 目標作業時間 */
    private int second;

    /** 達成しているかどうか */
    private Boolean achievementflag;

    /** 期限切れしているか */
    private Boolean expiredflag;

    /** いつ設定されたか */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}