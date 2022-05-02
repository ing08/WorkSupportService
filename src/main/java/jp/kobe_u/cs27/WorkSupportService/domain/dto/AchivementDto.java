package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;

/**
 * 成果
 *
 * @author ing
 */
@Data
public class AchivementDto {
    /** コメントID */
    private Long cid;

    /** 誰のコメントか */
    private String uid;

    /** どの作業についてのコメントか */
    private String wid;

    /** いつのコメントか */
    private String createdAt;

    /** いつのコメントか(日付のみ) */
    private String createdAtdate;

    /** コメント */
    private String comment;

    /**
     * コメントからDTOを作成
     *
     * @param Comment
     * @return AchivementDto
     */
    public static AchivementDto build(Comment comment) {
        AchivementDto dto = new AchivementDto();

        dto.cid = comment.getCid();
        dto.uid = comment.getUid();
        dto.wid = comment.getWid();

        Date createdAt = comment.getCreatedAt();

        if (createdAt != null) { // createdAtがnullでないならDate型をString型に変換
            dto.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        } else {
            dto.createdAt = "-----";
        }

        if (createdAt != null) { // createdAtがnullでないならDate型をString型(日付のみ)に変換
            String date = new SimpleDateFormat("MM-dd").format(createdAt);

            // 曜日所得
            Calendar cal = Calendar.getInstance();
            cal.setTime(createdAt);
            String week = "";
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY: // Calendar.SUNDAY:1 （値。意味はない）
                // 日曜日
                week = "(日)";

                break;
            case Calendar.MONDAY: // Calendar.MONDAY:2
                // 月曜日
                week = "(月)";

                break;
            case Calendar.TUESDAY: // Calendar.TUESDAY:3
                // 火曜日
                week = "(火)";

                break;
            case Calendar.WEDNESDAY: // Calendar.WEDNESDAY:4
                // 水曜日
                week = "(水)";

                break;
            case Calendar.THURSDAY: // Calendar.THURSDAY:5
                // 木曜日
                week = "(木)";

                break;
            case Calendar.FRIDAY: // Calendar.FRIDAY:6
                // 金曜日
                week = "(金)";

                break;
            case Calendar.SATURDAY: // Calendar.SATURDAY:7
                // 土曜日
                week = "(土)";

                break;
            }

            dto.createdAtdate = date + " " + week;

        } else {
            dto.createdAtdate = "-----";
        }

        dto.comment = comment.getComment();

        return dto;
    }
}
