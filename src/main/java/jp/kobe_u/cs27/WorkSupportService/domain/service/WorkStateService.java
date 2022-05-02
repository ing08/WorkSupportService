package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.UserValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkStateValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.CommentWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkStateWithNameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkStateWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkState;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.CommentRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.UserRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkLogRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkStateRepositry;

/**
 * 作業状況のサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class WorkStateService {
    /** 作業状況のリポジトリ */
    private final WorkStateRepositry wsr;
    /** ユーザのリポジトリ */
    private final UserRepositry ur;
    /** 作業のリポジトリ */
    private final WorkRepositry wr;
    /** 作業ログのリポジトリ */
    private final WorkLogRepositry wlr;
    /** コメントのリポジトリ */
    private final CommentRepositry cr;

    // /** ユーザのサービス */
    // private final UserService us;
    // /** 作業のサービス */
    // private final WorkService ws;
    // /** 作業ログのサービス */
    // private final WorkLogService wls;
    // /** コメントのサービス */
    // private final CommentService cs;

    /* -------------------- Create -------------------- */

    /**
     * 作業状況を新規登録
     *
     * @param uid
     * @return WorkState
     */
    public WorkState createWorkState(String uid, boolean discloseflag) {
        if (!ur.existsById(uid)) {// ユーザが存在するかどうかチェック
            throw new UserValidationException(USER_DOES_NOT_EXIST, "create the workState",
                    String.format("user %s does not exist", uid));
        }

        if (wsr.existsById(uid)) {// 作業状況においてuidがかぶっているかどうかチェック
            throw new WorkStateValidationException(WORKSTATE_ALREADY_EXISTS, "create the workState",
                    String.format("user %s workState already exists", uid));
        }

        return wsr.save(new WorkState(uid, "0", discloseflag)); // 作業状況をDBに登録し、登録した作業状況の情報を戻り値として返す
    }

    /* -------------------- Read -------------------- */

    /**
     * あるユーザの勉強状況を所得
     *
     * @param uid
     * @return WorkState
     */
    public WorkState getWorkState(String uid) {
        WorkState state = wsr.findById(uid).orElseThrow(() -> new WorkStateValidationException(WORKSTATE_DOES_NOT_EXIST,
                "get the workState", String.format("user %s workState does not exist", uid)));

        return state;
    }

    /**
     * あるユーザの勉強状況を作業の作業名付きで所得
     *
     * @param uid
     * @return WorkStateWithWnameDto
     */
    public WorkStateWithWnameDto getWorkStateWithWname(String uid) {
        WorkState state = wsr.findById(uid).orElseThrow(() -> new WorkStateValidationException(WORKSTATE_DOES_NOT_EXIST,
                "get the workState", String.format("user %s workState does not exist", uid)));

        Work work;
        String wid = state.getWid();

        if (!state.getWid().equals("0")) { // 作業している作業の作業IDが"0"でないなら
            work = wr.findById(wid).orElseThrow(() -> new WorkValidationException(WORK_DOES_NOT_EXIST, "get the work",
                    String.format("work %s does not exist", state.getWid()))); // 作業を所得
        } else {
            work = new Work(wid, "何もしていない");
        }

        WorkStateWithWnameDto dto = new WorkStateWithWnameDto();

        return dto.build(uid, work);
    }

    /**
     * 現在、ある作業が実行されているか確認
     *
     * @param wid
     * @return boolean
     */
    public boolean existsWorkedWork(String wid) {
        return wsr.existsByWid(wid);
    }

    // /**
    // * 全ての勉強状況を所得
    // *
    // * @return List<WorkState>
    // */
    // public List<WorkState> getAllWorkState() {
    // ArrayList<WorkState> list = new ArrayList<WorkState>();

    // for (WorkState state : wsr.findAll()) {
    // list.add(state);
    // }

    // return list;
    // }

    /**
     * 全ての作業状況をユーザのニックネームと作業の作業名付きで所得
     *
     * @return List<WorkStateDto>
     */
    public List<WorkStateWithNameDto> getAllWorkStateWithName() {
        ArrayList<WorkStateWithNameDto> list = new ArrayList<WorkStateWithNameDto>();

        User user;
        Work work;
        WorkLog log;
        String logwname;
        Comment comment;
        String commentwname;

        for (WorkState state : wsr.findByDiscloseflag(true)) {
            String uid = state.getUid();
            String wid = state.getWid();

            user = ur.findById(uid).orElseThrow(() -> new UserValidationException(USER_DOES_NOT_EXIST, "get the user",
                    String.format("user %s does not exist", uid))); // ユーザを所得

            if (!wid.equals("0")) { // 作業している作業の作業IDが"0"でないなら
                work = wr.findById(wid).orElseThrow(() -> new WorkValidationException(WORK_DOES_NOT_EXIST,
                        "get the work", String.format("work %s does not exist", wid))); // 作業を所得
            } else {
                work = new Work(wid, "-----");
            }

            WorkStateWithNameDto dto = new WorkStateWithNameDto();

            // dto.setUid(user.getUid());
            // dto.setNickname(user.getUid());
            // dto.setWid(work.getWid());
            // dto.setWname(work.getWname());

            log = wlr.findFirstByUidOrderBySinceDesc(uid); // 直前の作業ログを所得

            WorkLogWithWnameDto wlwwdto = new WorkLogWithWnameDto();

            if (log != null) { // 直近の作業ログがあるなら
                String earlierwid = log.getWid();

                if (!wr.existsById(earlierwid)) { // 作業が存在しない場合
                    logwname = "削除済み";
                } else {
                    logwname = wr.findById(earlierwid)
                            .orElseThrow(() -> new WorkValidationException(WORK_DOES_NOT_EXIST, "get the work",
                                    String.format("work %s does not exist", earlierwid)))
                            .getWname(); // 作業名を所得
                }
            } else {
                // wlwwdto.setUid(uid);
                // wlwwdto.setWid("0");
                // wlwwdto.setWname("-----");
                // wlwwdto.setSince("-----");
                // wlwwdto.setUntil("-----");
                // wlwwdto.setSecond(0);

                // log.setUid(uid);
                // log.setWid("0");
                // log.setSecond(0);

                log = new WorkLog(null, uid, "0", null, null, 0, null, null, null, null);

                logwname = "-----";
            }

            comment = cr.findFirstByUidAndDiscloseflagOrderByCreatedAtDesc(uid, true); // 直前の公開されているコメントを所得

            CommentWithWnameDto cwwdto = new CommentWithWnameDto();

            if (comment != null) { // 直近のコメントがあるなら
                String commentwid = comment.getWid();

                if (commentwid.equals("0")) {
                    commentwname = "未指定";
                } else if (!wr.existsById(commentwid)) { // 作業が存在しない場合
                    commentwname = "削除済み";
                } else {
                    commentwname = wr.findById(commentwid)
                            .orElseThrow(() -> new WorkValidationException(WORK_DOES_NOT_EXIST, "get the work",
                                    String.format("work %s does not exist", commentwid)))
                            .getWname(); // 作業名を所得
                }
            } else {
                // comment.setUid(uid);
                // comment.setWid("0");
                // comment.setComment("-----");

                comment = new Comment(null, uid, "0", null, "-----", true, false);

                commentwname = "-----";
            }

            list.add(dto.build(user, work, wlwwdto.build(log, logwname), cwwdto.build(comment, commentwname))); // リストにユーザのニックネームと作業の作業名付きの作業状況を追加
        }

        return list;
    }

    /**
     * 現在作業中のユーザの人数を所得
     *
     * @param uid
     * @return 現在作業中のユーザの人数
     */
    public int getNumberOfWorking(String uid) {
        return wsr.findByDiscloseflagAndWidNotAndUidNot(true, "0", uid).size();
    }

    /* -------------------- Update -------------------- */

    /**
     * 作業状況を更新
     *
     * @param uid
     * @param state
     * @return WorkState
     */
    public WorkState updateWorkState(String uid, WorkState state) {
        // ユーザIDを変数に格納する
        final String uidafter = state.getUid();

        // 作業状況が存在しない場合、例外を投げる
        if (!wsr.existsById(uid)) {
            throw new WorkStateValidationException(WORKSTATE_DOES_NOT_EXIST, "update the workState",
                    String.format("user %s workState does not exist", uid));
        }

        // uidが変更されている場合、例外を投げる
        if (!uidafter.equals(uid)) {
            throw new WorkStateValidationException(UID_CAN_NOT_BE_CHANGED, "update the workState",
                    String.format("uid %s can not be changed", uid));
        }

        // 作業が存在しない場合、例外を投げる
        if (!(wr.existsById(state.getWid()) || state.getWid().equals("0"))) {
            throw new WorkStateValidationException(WORK_DOES_NOT_EXIST, "update the workState",
                    String.format("work %s does not exist", state.getWid()));
        }

        return wsr.save(state); // DB上の作業状況情報を更新し、新しい作業状況を戻り値として返す
    }

    /* -------------------- Delete -------------------- */

    /**
     * 作業状況を削除
     *
     * @param uid
     */
    @Transactional
    public void deleteWorkState(String uid) {
        // 作業状況が存在しない場合、例外を投げる
        if (!wsr.existsById(uid)) {
            throw new WorkStateValidationException(WORKSTATE_DOES_NOT_EXIST, "delete the workState",
                    String.format("user %s workState does not exist", uid));
        }

        wsr.deleteById(uid);
    }
}
