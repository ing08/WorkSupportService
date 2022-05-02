package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkTimeChallenge;

/**
 * 目標作業時間と進捗
 *
 * @author ing
 */
@Data
public class WorkTimeChallengeDto {
    /** 目標作業時間ID */
    private Long wtcid;

    /** 誰のチャレンジ情報か */
    private String uid;

    /** いつからのチャレンジか */
    private Date since;

    /** いつまでのチャレンジか */
    private Date until;

    /** いつからのチャレンジか(日付のみ) */
    private String sincedatest;

    /** いつまでのチャレンジか(日付のみ) */
    private String untildatest;

    /** 現在までの作業時間 */
    private int worktime;

    /** 目標作業時間 */
    private int second;

    /** 達成しているかどうか */
    private Boolean achievementflag;

    /** 期限切れしているか */
    private Boolean expiredflag;

    /**
     * 目標作業時間と現在までの作業時間からDTOを作成
     *
     * @param challenge
     * @param worktime
     * @return WorkTimeChallengeDto
     */
    public static WorkTimeChallengeDto build(WorkTimeChallenge challenge, int worktime) {
        WorkTimeChallengeDto dto = new WorkTimeChallengeDto();

        dto.wtcid = challenge.getWtcid();
        dto.uid = challenge.getUid();
        dto.since = challenge.getSince();
        dto.until = challenge.getUntil();

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        dto.sincedatest = sdFormat.format(dto.since);

        // 1日減算
        cal.setTime(challenge.getUntil());
        cal.add(Calendar.DATE, -1);
        Date until = cal.getTime();

        dto.untildatest = sdFormat.format(until);

        dto.worktime = worktime;
        dto.second = challenge.getSecond();
        dto.achievementflag = challenge.getAchievementflag();
        dto.expiredflag = challenge.getExpiredflag();

        return dto;
    }
}