package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;

/**
 * ユーザのニックネームと作業の作業名付きの作業状況
 *
 * @author ing
 */
@Data
public class WorkStateWithNameDto {
    /** ユーザID */
    private String uid;

    /** ニックネーム */
    private String nickname;

    /** 作業中の作業ID */
    private String wid;

    /** 作業中の作業名 */
    private String wname;

    /** 作業イベントID */
    private Long lid;

    /** 直近の作業ID */
    private String earlierwid;

    /** 直近の作業名 */
    private String earlierwname;

    /** 何時から */
    private String since;

    /** 何時まで */
    private String until;

    /** 何秒作業したか */
    private int second;

    /** コメントID */
    private Long cid;

    /** どの作業についてのコメントか */
    private String commentwid;

    /** 作業名 */
    private String commentwname;

    /** いつのコメントか */
    private String createdAt;

    /** コメント */
    private String comment;

    /**
     * UserとWorkとWorkLogWithWnameDtoからDTOを生成
     *
     * @param user
     * @param work
     * @param wlwwdto
     * @return WorkStateWithNameDto
     */
    public static WorkStateWithNameDto build(User user, Work work, WorkLogWithWnameDto wlwwdto,
            CommentWithWnameDto cwwdto) {
        WorkStateWithNameDto dto = new WorkStateWithNameDto();

        dto.uid = user.getUid();
        dto.nickname = user.getNickname();
        dto.wid = work.getWid();
        dto.wname = work.getWname();

        if (wlwwdto != null) { // WorkLogWithWnameDtoがnullでなければ
            dto.lid = wlwwdto.getLid();
            dto.earlierwid = wlwwdto.getWid();
            dto.earlierwname = wlwwdto.getWname();
            dto.since = wlwwdto.getSince();
            dto.until = wlwwdto.getUntil();
            dto.second = wlwwdto.getSecond();
        }

        if (cwwdto != null) { // CommentWithWnameDtoがnullでなければ
            dto.cid = cwwdto.getCid();
            dto.commentwid = cwwdto.getWid();
            dto.commentwname = cwwdto.getWname();
            dto.createdAt = cwwdto.getCreatedAt();
            dto.comment = cwwdto.getComment();
        }

        return dto;
    }
}
