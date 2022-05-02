package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.Date;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkEventValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkEvent;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkState;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkEventRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkLogRepositry;

/**
 * 作業イベントのサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class WorkEventService {
    /** 作業イベントのリポジトリ */
    private final WorkEventRepositry wer;
    /** 作業ログのリポジトリ */
    private final WorkLogRepositry wlr;

    /** 作業状況のサービス */
    private final WorkStateService wss;
    /** 作業のサービス */
    private final WorkService ws;
    // /** 作業ログのサービス */
    // private final WorkLogService wls;
    /** ログインログのサービス */
    private final LoginLogService lls;

    /* -------------------- Create -------------------- */

    /**
     * 作業開始イベントを作成
     *
     * @param uid
     * @param wid
     * @return WorkEvent
     */
    public WorkEvent startWorkEvent(String uid, String wid) {
        WorkState state = wss.getWorkState(uid); // ユーザの作業状況を所得
        String workingwid = state.getWid();

        if (!workingwid.equals("0") && !workingwid.equals(wid)) { // 現在作業中なら
            this.endWorkEvent(uid, workingwid); // その作業をend
        }

        WorkEvent we = wer.findFirstByUidOrderByCreatedAtDesc(uid); // 直前のWorkEventを所得

        if (we != null && !we.getType().equals("end")) { // 直前のWorkEventがnullでなく、typeがendでなかった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH, "create the WorkEvent",
                    String.format("startWorkEvent does not match endWorkEvent %s just before", we.getEid())); // 例外を投げる
        }

        // 作業状況を更新
        state.setWid(wid);
        wss.updateWorkState(uid, state);

        Date lastlogin = new Date();

        WorkEvent startwe = wer.save(new WorkEvent(null, uid, wid, lastlogin, "start"));// startWorkEventを記録

        lls.updateLoginLog(uid, lastlogin); // ログインログを更新

        return startwe;
    }

    /**
     * 作業開始イベントを作業名指定で作成(RestController用)
     *
     * @param uid
     * @param wname
     * @return WorkEvent
     */
    public WorkEvent startWorkEventByWname(String uid, String wname) {
        WorkState state = wss.getWorkState(uid); // ユーザの作業状況を所得
        String workingwid = state.getWid();

        String wid = ws.getWorkByWname(wname).getWid(); // wnameからwidを所得

        if (!workingwid.equals("0") && !workingwid.equals(wid)) { // 現在作業中なら
            this.endWorkEvent(uid, workingwid); // その作業をend
        }

        WorkEvent we = wer.findFirstByUidOrderByCreatedAtDesc(uid); // 直前のWorkEventを所得

        if (we != null && !we.getType().equals("end")) { // 直前のWorkEventがnullでなく、typeがendでなかった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH, "create the WorkEvent",
                    String.format("startWorkEvent does not match endWorkEvent %s just before", we.getEid())); // 例外を投げる
        }

        // 作業状況を更新
        state.setWid(wid);
        wss.updateWorkState(uid, state);

        Date lastlogin = new Date();

        WorkEvent startwe = wer.save(new WorkEvent(null, uid, wid, lastlogin, "start"));// startWorkEventを記録

        lls.updateLoginLog(uid, lastlogin); // ログインログを更新

        return startwe;
    }

    /**
     * 作業開始イベントを手動で作成されたログに基づいて作成
     *
     * @param uid
     * @param wid
     * @param since
     * @return WorkEvent
     */
    public WorkEvent startWorkEventManually(String uid, String wid, Date since) {
        return wer.save(new WorkEvent(null, uid, wid, since, "start"));
    }

    /**
     * 作業終了イベントを作成
     *
     * @param uid
     * @param wid
     * @return WorkEvent
     */
    public WorkEvent endWorkEvent(String uid, String wid) {
        WorkEvent startwe = wer.findFirstByUidOrderByCreatedAtDesc(uid); // 直前のWorkEventを所得

        if (!startwe.getType().equals("start")) { // 直前のWorkEventのtypeがstart出なかった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH, "create the WorkEvent",
                    String.format("startWorkEvent does not match endWorkEvent %s just before", startwe.getEid())); // 例外を投げる
        }

        if (!wid.equals(startwe.getWid())) { // 直前のstartWorkEventの作業IDが作成しようとしているendWorkEventと異なった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH_WID, "create the WorkEvent", String
                    .format("endWorkEvent %s does not match startWorkEvent %s just before", wid, startwe.getWid())); // 例外を投げる
        }

        WorkState state = wss.getWorkState(uid); // ユーザの作業状況を所得

        // 作業状況を更新
        state.setWid("0");
        wss.updateWorkState(uid, state);

        WorkEvent endwe = wer.save(new WorkEvent(null, uid, wid, new Date(), "end")); // endWorkEventを記録

        // wls.createWorkLog(startwe, endwe); // 作業ログを記録

        int second = (int) (endwe.getCreatedAt().getTime() - startwe.getCreatedAt().getTime()) / 1000; // 作業時間sを計算

        Date lastlogin = new Date();

        wlr.save(new WorkLog(null, endwe.getUid(), endwe.getWid(), startwe.getCreatedAt(), endwe.getCreatedAt(), second,
                startwe.getEid(), endwe.getEid(), lastlogin, true)); // 作業ログを作成

        lls.updateLoginLog(uid, lastlogin); // ログインログを更新

        return endwe;
    }

    /**
     * 作業終了イベントを作業名指定で作成(RestController用)
     *
     * @param uid
     * @param wname
     * @return WorkEvent
     */
    public WorkEvent endWorkEventByWname(String uid, String wname) {
        WorkEvent startwe = wer.findFirstByUidOrderByCreatedAtDesc(uid); // 直前のWorkEventを所得

        if (!startwe.getType().equals("start")) { // 直前のWorkEventのtypeがstart出なかった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH, "create the WorkEvent",
                    String.format("startWorkEvent does not match endWorkEvent %s just before", startwe.getEid())); // 例外を投げる
        }

        String wid = ws.getWorkByWname(wname).getWid(); // wnameからwidを所得

        if (!wid.equals(startwe.getWid())) { // 直前のstartWorkEventの作業IDが作成しようとしているendWorkEventと異なった場合
            throw new WorkEventValidationException(EVENT_START_END_NOT_MATCH_WID, "create the WorkEvent", String
                    .format("endWorkEvent %s does not match startWorkEvent %s just before", wid, startwe.getWid())); // 例外を投げる
        }

        WorkState state = wss.getWorkState(uid); // ユーザの作業状況を所得

        // 作業状況を更新
        state.setWid("0");
        wss.updateWorkState(uid, state);

        WorkEvent endwe = wer.save(new WorkEvent(null, uid, wid, new Date(), "end")); // endWorkEventを記録

        // wls.createWorkLog(startwe, endwe); // 作業ログを記録

        int second = (int) (endwe.getCreatedAt().getTime() - startwe.getCreatedAt().getTime()) / 1000; // 作業時間sを計算

        Date lastlogin = new Date();

        wlr.save(new WorkLog(null, endwe.getUid(), endwe.getWid(), startwe.getCreatedAt(), endwe.getCreatedAt(), second,
                startwe.getEid(), endwe.getEid(), lastlogin, true)); // 作業ログを作成

        lls.updateLoginLog(uid, lastlogin); // ログインログを更新

        return endwe;
    }

    /**
     * 作業終了イベントを手動で作成されたログに基づいて作成
     *
     * @param uid
     * @param wid
     * @param until
     * @return WorkEvent
     */
    public WorkEvent endWorkEventManually(String uid, String wid, Date until) {
        return wer.save(new WorkEvent(null, uid, wid, until, "end"));
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザの作業イベントで指定日時以前最初にあるものを所得
     *
     * @param uid
     * @param date
     * @return WorkEvent
     */

    public WorkEvent getNewestWorkEventSince(String uid, Date date) {
        return wer.findFirstByUidAndCreatedAtLessThanEqualOrderByCreatedAtDesc(uid, date);
    }

    /**
     * ユーザの作業イベントで指定日時以後最初にあるものを所得
     *
     * @param uid
     * @param date
     * @return WorkEvent
     */
    public WorkEvent getOldestWorkEventUntil(String uid, Date date) {
        return wer.findFirstByUidAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(uid, date);
    }

    /* -------------------- Delete -------------------- */

    /**
     * 作業イベントを削除
     *
     * @param eid
     */
    @Transactional
    public void deleteWorkEventByEid(Long eid) {
        if (!wer.existsById(eid)) { // 作業イベントが存在しない場合
            throw new WorkEventValidationException(WORKEVENT_DOES_NOT_EXIST, "delete the workEvent",
                    String.format("workEvent %s does not exist", eid)); // 例外を投げる
        }

        wer.deleteById(eid);
    }

    /**
     * ユーザに紐づくすべての作業イベント削除
     *
     * @param uid
     */
    public void deleteAllWorkEventByUid(String uid) {
        wer.deleteByUid(uid);
    }
}