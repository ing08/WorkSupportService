package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;

/**
 * 作業ログフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentForm {
    /** どの作業についてのコメントか */
    @NotNull
    private String wid;

    /** 誰のコメントか */
    @NotNull
    private String uid;

    /** コメント */
    @NotNull
    private String comment;

    /** コメントを公開するか */
    @NotNull
    private boolean discloseflag;

    /** 成果かどうか */
    @NotNull
    private boolean achievementflag;

    /**
     * フォームをエンティティに変換
     *
     * @return Work
     */
    public Comment toEntity() {
        return new Comment(null, this.getUid(), this.getWid(), null, this.getComment(), this.isDiscloseflag(),
                this.isAchievementflag());
    }
}
