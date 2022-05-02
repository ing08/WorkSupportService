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
 * コメントのエンティティ
 *
 * @author ing
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /** コメントID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;

    /** 誰のコメントか */
    private String uid;

    /** どの作業についてのコメントか */
    private String wid;

    /** いつのコメントか */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /** コメント */
    private String comment;

    /** コメントを公開するか */
    private boolean discloseflag;

    /** 成果かどうか */
    private boolean achievementflag;
}
