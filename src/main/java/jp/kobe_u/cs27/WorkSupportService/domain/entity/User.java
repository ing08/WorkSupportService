package jp.kobe_u.cs27.WorkSupportService.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

/**
 * ユーザのエンティティ
 *
 * @author ing
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** ユーザID */
    @Id
    private String uid;

    /** ニックネーム */
    private String nickname;

    /** メールアドレス */
    private String email;

    /** 情報を公開するか */
    private boolean discloseflag;

    /** 作成日時 */
    private Date CreatedAt;

    /** ToDoリストのユーザID */
    private String todoid;
}
