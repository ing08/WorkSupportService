package jp.kobe_u.cs27.WorkSupportService.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 作業イベントログのエンティティ
 *
 * @author ing
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkState {
    /** あるユーザが */
    @Id
    private String uid;

    /** 作業中かどうか 0なら何もしていない */
    private String wid;

    /** 情報を公開するか */
    private boolean discloseflag;
}
