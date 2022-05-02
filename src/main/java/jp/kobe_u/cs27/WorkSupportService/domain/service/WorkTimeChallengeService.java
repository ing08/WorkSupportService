package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import jp.kobe_u.cs27.WorkSupportService.configration.exception.OtherValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkTimeChallengeValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeChallengeDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkTimeChallenge;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkTimeChallengeRepositry;

/**
 * 目標作業時間のサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class WorkTimeChallengeService {
    /** 目標作業時間のリポジトリ */
    private final WorkTimeChallengeRepositry wtcr;

    /** 作業ログのサービス */
    private final WorkLogService wls;
    /** コメントのサービス */
    private final CommentService cs;

    /* -------------------- Create -------------------- */

    /**
     * 目標作業時間を設定
     *
     * @param challenge
     * @return WorkTimeChallenge
     */
    public WorkTimeChallenge createWorkTimeChallenge(String uid, String untilst, int second) {
        Date now = new Date(); // 今日の日付を所得
        Date until = new Date();
        Date nowdate = new Date();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        String nowst = sdFormat.format(now);

        try {
            // String型をDate型に変換する
            until = sdFormat.parse(untilst);
            nowdate = sdFormat.parse(nowst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the workTimeChallenge",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        if (until.before(now)) { // 目標日時が今より過去だったら
            throw new WorkTimeChallengeValidationException(UNTIL_CAN_NOT_SET_IN_THE_PAST,
                    "create the workTimeChallenge", String.format("until %s can not set in the past", until)); // 例外を投げる
        }

        return wtcr.save(new WorkTimeChallenge(null, uid, nowdate, until, second, false, false, now)); // 目標作業時間をDBに登録
    }

    /* -------------------- Read -------------------- */

    /**
     * 現在の目標作業時間を所得
     *
     * @param uid
     * @return WorkTimeChallengeDto
     */
    public WorkTimeChallengeDto getWorkTimeChallenge(String uid) {
        WorkTimeChallenge challenge = wtcr.findFirstByUidOrderByCreatedAtDesc(uid); // 現在の目標作業時間を所得

        if (challenge == null) { // 目標作業時間が設定されていないなら
            return null;
        }

        Calendar cal = Calendar.getInstance();

        // 1日減算
        cal.setTime(challenge.getUntil());
        cal.add(Calendar.DATE, -1);
        Date until = cal.getTime();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        // Date型をString型に変換
        String sincest = sdformat.format(challenge.getSince());
        String untilst = sdformat.format(until);

        List<WorkTimeWithWnameDto> times = wls
                .caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, sincest, untilst)); // 目標作業時間の期間の作業時間を計算

        int worktime = times.get(times.size() - 1).getSecond(); // 作業時間の合計を所得

        Date now = new Date();

        WorkTimeChallengeDto dto = new WorkTimeChallengeDto();

        if (worktime >= challenge.getSecond()) { // 目標作業時間を達成していれば
            if (!challenge.getAchievementflag()) { // 作業時間を達成したことが今わかった場合
                cs.createComment(new Comment(null, challenge.getUid(), "0", now,
                        "期間" + sincest + "~" + untilst + "で、目標作業時間 : "
                                + new BigDecimal(challenge.getSecond() / 3600).setScale(1) + "時間達成！",
                        false, true)); // 成果に追加
            }

            challenge.setAchievementflag(true); // 達成しているかフラグをtrueに

            wtcr.save(challenge); // 目標作業時間を更新

            return dto.build(challenge, worktime); // 目標作業時間DTOを返す
        } else { // 作業ログ削除対策
            challenge.setAchievementflag(false); // 達成しているかフラグをfalseに

            wtcr.save(challenge); // 目標作業時間を更新
        }

        if (challenge.getUntil().before(now)) { // 達成できずに期限切れしていた場合
            challenge.setExpiredflag(true); // 期限切れしているかフラグをtrueに

            wtcr.save(challenge); // 目標作業時間を更新
        }

        return dto.build(challenge, worktime);
    }

    /* -------------------- Delete -------------------- */

    /**
     * 現在の目標作業時間を削除
     *
     * @param uid
     * @param wtcid
     */
    @Transactional
    public void deleteWorkTimeChallenge(String uid, Long wtcid) {
        WorkTimeChallenge challenge = wtcr.findById(wtcid)
                .orElseThrow(() -> new WorkTimeChallengeValidationException(WORKTIMECHALLENGE_DOES_NOT_EXIST,
                        "delete the workTimeChallenge", String.format("workTimeChallenge %s does not exist", wtcid))); // 目標作業時間を所得、存在しなければ例外を投げる

        String challengeuid = challenge.getUid();

        if (!uid.equals(challengeuid)) { // 消そうとしている目標作業時間のユーザIDが消そうとしているユーザのものと異なる場合(REST用)
            throw new WorkTimeChallengeValidationException(UID_DOSE_NOT_MATCH, "delete the workTimeChallenge",
                    String.format("workTimeChallenge uid %s dose not match deleting uid %s", challengeuid, uid)); // 例外を投げる
        }

        wtcr.deleteById(wtcid); // 目標作業時間を削除
    }

    /**
     * ユーザに紐づくすべての目標作業時間を削除
     *
     * @param uid
     */
    @Transactional
    public void deleteAllWorkTimeChallengeByUid(String uid) {
        wtcr.deleteByUid(uid);
    }
}