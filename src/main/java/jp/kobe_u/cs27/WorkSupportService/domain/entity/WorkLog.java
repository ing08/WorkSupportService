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
 * 作業ログのエンティティ
 *
 * @author ing
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WorkLog {
    /** 作業イベントID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lid;

    /** 誰のログか */
    private String uid;

    /** どの作業を */
    private String wid;

    /** 何時から */
    @Temporal(TemporalType.TIMESTAMP)
    private Date since;

    /** 何時まで */
    @Temporal(TemporalType.TIMESTAMP)
    private Date until;

    /** 何秒作業したか */
    private int second;

    /** 開始イベントID */
    private Long startEvent;

    /** 終了イベントID */
    private Long endEvent;

    /** 作成日時 */
    private Date createdAt;

    /** リアルタイムで入力されたか */
    private Boolean realtimeflag;
}
