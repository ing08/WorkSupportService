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
 * 作業イベントのエンティティ
 *
 * @author ing
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WorkEvent {
    /** 作業ログID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eid;

    /** 誰のイベントか */
    private String uid;

    /** どの作業を */
    private String wid;

    /** いつのイベントか */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /** どのタイプのイベントか */
    private String type;

    // /** コメント */
    // private String comment;
}
