package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;

/**
 * 作業名付きのコメント
 *
 * @author ing
 */
@Data
public class CommentWithWnameDto {
    /** コメントID */
    private Long cid;

    /** 誰のコメントか */
    private String uid;

    /** どの作業についてのコメントか */
    private String wid;

    /** 作業名 */
    private String wname;

    /** いつのコメントか */
    private String createdAt;

    /** いつのコメントか(日付のみ) */
    private String createdAtdate;

    /** コメント */
    private String comment;

    /** コメントを公開するか */
    private String discloseflag;

    /** 成果かどうか */
    private Boolean achievementflag;

    /**
     * コメントと作業名からDTOを作成
     *
     * @param Comment
     * @param wname
     * @return CommentWithWname
     */
    public static CommentWithWnameDto build(Comment comment, String wname) {
        CommentWithWnameDto dto = new CommentWithWnameDto();

        dto.cid = comment.getCid();
        dto.uid = comment.getUid();
        dto.wid = comment.getWid();
        dto.wname = wname;

        Date createdAt = comment.getCreatedAt();

        if (createdAt != null) { // createdAtがnullでないならDate型をString型に変換
            dto.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        } else {
            dto.createdAt = "-----";
        }

        if (createdAt != null) { // createdAtがnullでないならDate型をString型(日付のみ)に変換
            dto.createdAtdate = new SimpleDateFormat("yyyy-MM-dd").format(createdAt);
        } else {
            dto.createdAt = "-----";
        }

        dto.comment = comment.getComment();

        if (comment.isDiscloseflag()) {
            dto.discloseflag = "公開する";
        } else {
            dto.discloseflag = "公開しない";
        }

        // if (comment.isAchievementflag()) {
        // dto.achievementflag = "成果";
        // } else {
        // dto.achievementflag = "成果でない";
        // }

        dto.achievementflag = comment.isAchievementflag();

        return dto;
    }
}
