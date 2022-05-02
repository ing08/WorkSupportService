package jp.kobe_u.cs27.WorkSupportService.application.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 成果フォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchivementForm {
    /** どの作業についてのコメントか */
    @NotNull
    private String wid;

    /** 誰のコメントか */
    private String uid;

    /** いつのコメントか(日付) */
    @NotNull
    private String date;

    /** いつのコメントか(時刻) */
    @NotNull
    private String time;

    /** コメント */
    @NotNull
    private String comment;

    /** コメントを公開するか */
    @NotNull
    private boolean discloseflag;

    /**
     * フォームをエンティティに変換
     *
     * @return Work
     */
    public Comment toEntity() {
        Date createdAt = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // String型をDate型に変換する
            createdAt = sdFormat.parse(this.date + " " + this.time);
        } catch (ParseException e) {
        }

        return new Comment(null, this.getUid(), this.getWid(), createdAt, this.getComment(), this.isDiscloseflag(),
                true);
    }
}
