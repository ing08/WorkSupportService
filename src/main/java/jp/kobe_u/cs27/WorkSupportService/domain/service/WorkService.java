package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkRepositry;

/**
 * 作業のサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class WorkService {
    /** 作業のリポジトリ */
    private final WorkRepositry wr;

    /** 作業状況のサービス */
    private final WorkStateService wss;

    /* -------------------- Create -------------------- */

    /**
     * 作業新規登録
     *
     * @param work
     * @return Work
     */
    public Work createWork(Work work) {
        String wid = work.getWid();

        if (wr.existsById(wid)) {// widがかぶっているかどうかチェック
            throw new WorkValidationException(WORK_ALREADY_EXISTS, "create the work",
                    String.format("work %s already exists", wid));
        }

        // if (work.getWname() == null) { // wnameがnullの場合、例外を投げる
        // throw new WorkValidationException(WORK_WNAME_CAN_NOT_BE_SET_NULL, "create the
        // work",
        // String.format("work %s wname can not be set null", work.getWid()));
        // }

        return wr.save(work); // 作業をDBに登録し、登録した作業の情報を戻り値として返す
    }

    /* -------------------- Read -------------------- */

    /**
     * 指定した作業が存在するか確認する
     *
     * @param wid
     * @return boolean
     */
    public boolean existsWork(String wid) {
        return wr.existsById(wid);
    }

    /**
     * 作業を所得する
     *
     * @param wid
     * @return Work
     */
    public Work getWork(String wid) {
        Work work = wr.findById(wid).orElseThrow(() -> new WorkValidationException(WORK_DOES_NOT_EXIST, "get the work",
                String.format("work %s does not exist", wid))); // 作業を所得

        return work;
    }

    /**
     * 作業名で作業を所得
     *
     * @param wname
     * @return Work
     */
    public Work getWorkByWname(String wname) {
        Work work;

        if ((work = wr.findByWname(wname)) == null) { // 存在しない作業の作業名だった場合
            throw new WorkValidationException(WORK_DOES_NOT_EXIST, "get the work",
                    String.format("work name %s does not exist", wname));
        }

        return work;
    }

    /**
     * 全ての作業を所得する
     *
     * @return List<Work>
     */
    public List<Work> getAllWork() {
        ArrayList<Work> list = new ArrayList<Work>();

        for (Work work : wr.findAll()) {
            list.add(work);
        }

        return list;
    }

    /* -------------------- Update -------------------- */

    /**
     * 作業の情報を更新する
     *
     * @param Work
     * @return Work
     */
    @Transactional
    public Work updateWork(String wid, Work work) {
        // 作業IDを変数に格納する
        final String widafter = work.getWid();

        // 作業が存在しない場合、例外を投げる
        if (!wr.existsById(wid)) {
            throw new WorkValidationException(WORK_DOES_NOT_EXIST, "update the work",
                    String.format("work %s does not exist", wid));
        }

        // 作業中のユーザがいる場合、例外を投げる
        if (wss.existsWorkedWork(wid)) {
            throw new WorkValidationException(WORKED_WORK_CAN_NOT_BE_UPDETED, "update the work",
                    String.format("wid %s can not be updated during be worked", wid));
        }

        // widが変更されている場合、例外を投げる
        if (!widafter.equals(wid)) {
            throw new WorkValidationException(WID_CAN_NOT_BE_CHANGED, "update the work",
                    String.format("wid %s can not be changed", wid));
        }

        // 作業状況関連処理

        // DB上のユーザ情報を更新し、新しいユーザ情報を戻り値として返す
        return wr.save(work);
    }

    /* -------------------- Delete -------------------- */

    /**
     * 作業を削除する
     *
     * @param wid
     */
    @Transactional
    public void deleteWork(String wid) {
        if (!wr.existsById(wid)) { // 削除する作業が存在するかのチェック
            throw new WorkValidationException(WORK_DOES_NOT_EXIST, "delete the work",
                    String.format("work %s does not exist", wid));
        }

        // 作業中のユーザがいる場合、例外を投げる
        if (wss.existsWorkedWork(wid)) {
            throw new WorkValidationException(WORKED_WORK_CAN_NOT_BE_DELETED, "delete the work",
                    String.format("wid %s can not be deleted during be worked", wid));
        }

        wr.deleteById(wid); // 作業を削除する
    }
}
