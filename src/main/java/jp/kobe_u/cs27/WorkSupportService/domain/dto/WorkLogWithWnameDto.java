package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;

/**
 * 作業名付きの作業ログ
 *
 * @author ing
 */
@Data
public class WorkLogWithWnameDto {
    /** 作業ログID */
    private Long lid;

    /** 誰のログか */
    private String uid;

    /** どの作業を */
    private String wid;

    /** 作業名 */
    private String wname;

    /** 何時から */
    private String since;

    /** 何時まで */
    private String until;

    /** 何時から(Date型) */
    private Date sincedateformat;

    /** 何時まで(Date型) */
    private Date untildateformat;

    /** 何時まで(日付のみ) */
    private String untildate;

    /** 何時から(時間のみ) */
    private String sincetime;

    /** 何時まで(時間のみ) */
    private String untiltime;

    /** 何秒作業したか */
    private int second;

    /** 作成日時(日付だけ) */
    private String createdAtdate;

    /** リアルタイムで入力したか */
    private Boolean realtimeflag;

    /**
     * 作業ログと作業名からDTOを作成
     *
     * @param WorkLog
     * @param wname
     * @return WorkLogWithWnameDto
     */
    public static WorkLogWithWnameDto build(WorkLog log, String wname) {
        WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

        dto.lid = log.getLid();
        dto.uid = log.getUid();
        dto.wid = log.getWid();
        dto.wname = wname;

        dto.sincedateformat = log.getSince();
        dto.untildateformat = log.getUntil();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        if (dto.sincedateformat != null) { // sincedateformatがnullでないならDate型をString型に変換
            dto.since = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dto.sincedateformat);
            dto.sincetime = new SimpleDateFormat("HH:mm:ss").format(dto.sincedateformat);

            if (sdformat.format(dto.sincedateformat).equals(sdformat.format(dto.untildateformat))) { // 作業が日をまたいでいれば
                dto.sincetime = "2000/01/05 " + dto.sincetime;
            } else {
                dto.sincetime = "2000/01/04 " + dto.sincetime;
            }

            // dto.sincetime = "2000/01/05 " + dto.sincetime;
        } else {
            dto.since = "-----";
        }

        if (dto.untildateformat != null) { // untildateformatがnullでないならDate型をString型に変換
            dto.until = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dto.untildateformat);
            dto.untiltime = new SimpleDateFormat("HH:mm:ss").format(dto.untildateformat);

            dto.untiltime = "2000/01/05 " + dto.untiltime;

            // if (new SimpleDateFormat("yyyy-MM-dd").format(dto.sincedateformat).equals(
            // new SimpleDateFormat("yyyy-MM-dd").format(dto.untildateformat))) { //
            // 作業が日をまたいでいれば
            // dto.untiltime = "2000/01/05 " + dto.untiltime;
            // } else {
            // dto.untiltime = "2000/01/06 " + dto.untiltime;
            // }
        } else {
            dto.until = "-----";
        }

        if (dto.untildateformat != null) { // untildateformatがnullでないならDate型をString型(日付のみ)に変換
            dto.untildate = sdformat.format(dto.untildateformat);
        } else {
            dto.untildate = "-----";
        }

        dto.second = log.getSecond();

        Date createdAt = log.getCreatedAt();

        if (createdAt != null) {
            dto.createdAtdate = sdformat.format(createdAt);
        }

        dto.realtimeflag = log.getRealtimeflag();

        return dto;
    }
}
