package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;

import jp.kobe_u.cs27.WorkSupportService.configration.exception.CommentValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.OtherValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.AchivementDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.CommentWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.CommentRepositry;

/**
 * コメントのサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    /** コメントのリポジトリ */
    private final CommentRepositry cr;

    private final WorkService ws;

    /* -------------------- Create -------------------- */

    /**
     * コメントを作成
     *
     * @param comment
     * @return Comment
     */
    public Comment createComment(Comment comment) {
        String wid = comment.getWid();

        if ((!ws.existsWork(wid)) && (!wid.equals("0"))) {// 作業が存在するかどうかチェック
            throw new WorkValidationException(WORK_DOES_NOT_EXIST, "create the comment",
                    String.format("work %s does not exist", wid));
        }

        return cr.save(new Comment(null, comment.getUid(), comment.getWid(), new Date(), comment.getComment(),
                comment.isDiscloseflag(), comment.isAchievementflag())); // コメントをDBに登録
    }

    /**
     * 成果を作成
     *
     * @param Comment
     * @return Comment
     */
    public Comment createAchivement(Comment comment) {
        String wid = comment.getWid();

        if ((!ws.existsWork(wid)) && (!wid.equals("0"))) {// 作業が存在するかどうかチェック
            throw new WorkValidationException(WORK_DOES_NOT_EXIST, "create the comment",
                    String.format("work %s does not exist", wid));
        }

        return cr.save(comment); // 成果をDBに登録
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザに紐づくすべてのコメントを作業名付きで所得
     *
     * @param uid
     * @return List<CommentWithWnameDto>
     */
    public List<CommentWithWnameDto> getAllCommentByUid(String uid) {
        ArrayList<CommentWithWnameDto> list = new ArrayList<CommentWithWnameDto>();

        String wname;

        for (Comment comment : cr.findAllByUidOrderByCreatedAtDesc(uid)) {
            String wid = comment.getWid();

            if (wid.equals("0")) { // コメントの対象の作業が未指定だった場合
                wname = "未指定";
            } else {
                if (!ws.existsWork(wid)) { // 作業が存在しないなら
                    wname = "削除済み";
                } else {
                    wname = ws.getWork(wid).getWname(); // 作業名を所得
                }
            }

            CommentWithWnameDto dto = new CommentWithWnameDto();

            list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
        }

        return list;
    }

    // /**
    // * ユーザに紐づく直前のコメントを作業名付きで所得
    // *
    // * @param uid
    // * @return CommentWithWnameDto
    // */
    // public CommentWithWnameDto getNewestCommentByUid(String uid) {
    // Comment comment =
    // cr.findFirstByUidAndByDiscloseflagOrderByCreatedAtDesc(uid); //
    // 直前の公開されているコメントを所得

    // String wid = comment.getWid();
    // String wname;

    // if (wid.equals("0")) { // コメントの対象の作業が未指定だった場合
    // wname = "未指定";
    // } else {
    // if (!ws.existsWork(wid)) { // 作業が存在しないなら
    // wname = "削除済み";
    // } else {
    // wname = ws.getWork(wid).getWname(); // 作業名を所得
    // }
    // }

    // CommentWithWnameDto dto = new CommentWithWnameDto();

    // return dto.build(comment, wname); // 作業名付きのコメント
    // }

    /**
     * ユーザのコメントを期間を指定して所得
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return List<CommentWithWnameDto>
     */
    public List<CommentWithWnameDto> getCommentSettingPeriod(String uid, String sincest, String untilst) {
        ArrayList<CommentWithWnameDto> list = new ArrayList<CommentWithWnameDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        String wname;

        for (Comment comment : cr.findByUidAndCreatedAtBetweenOrderByCreatedAtDesc(uid, since, until)) {
            String wid = comment.getWid();

            if (wid.equals("0")) { // コメントの対象の作業が未指定だった場合
                wname = "未指定";
            } else {
                if (!ws.existsWork(wid)) { // 作業が存在しないなら
                    wname = "削除済み";
                } else {
                    wname = ws.getWork(wid).getWname(); // 作業名を所得
                }
            }

            CommentWithWnameDto dto = new CommentWithWnameDto();

            list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
        }

        return list;
    }

    /**
     * ユーザと作業に紐づくコメントを作業名付きで所得
     *
     * @param uid
     * @param wid
     * @return List<CommentWithWnameDto>
     */
    public List<CommentWithWnameDto> getCommentByWid(String uid, String wid) {
        ArrayList<CommentWithWnameDto> list = new ArrayList<CommentWithWnameDto>();

        String wname;

        if (wid.equals("0")) { // コメントの対象の作業が未指定だった場合
            for (Comment comment : cr.findAllByUidOrderByCreatedAtDesc(uid)) {
                wid = comment.getWid();

                if (wid.equals("0")) { // コメントの対象作業が未指定だった場合
                    wname = "未指定";

                    CommentWithWnameDto dto = new CommentWithWnameDto();

                    list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
                } else if (!ws.existsWork(wid)) { // コメントの対象作業が削除済みだった場合
                    wname = "削除済み";

                    CommentWithWnameDto dto = new CommentWithWnameDto();

                    list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
                }
            }
        } else {
            for (Comment comment : cr.findAllByUidAndWidOrderByCreatedAtDesc(uid, wid)) {
                // String wid = comment.getWid();

                wname = ws.getWork(wid).getWname(); // 作業名を所得

                CommentWithWnameDto dto = new CommentWithWnameDto();

                list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
            }
        }

        return list;
    }

    /**
     * ユーザのコメントを作業と期間を指定して所得
     *
     * @param uid
     * @param wid
     * @param sincest
     * @param untilst
     * @return List<CommentWithWnameDto>
     */
    public List<CommentWithWnameDto> getCommentByWidSettingPeriod(String uid, String wid, String sincest,
            String untilst) {
        ArrayList<CommentWithWnameDto> list = new ArrayList<CommentWithWnameDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        String wname;

        if (wid.equals("0")) { // コメントの対象の作業が未指定だった場合
            for (Comment comment : cr.findByUidAndCreatedAtBetweenOrderByCreatedAtDesc(uid, since, until)) {
                wid = comment.getWid();

                if (wid.equals("0")) { // コメントの対象作業が未指定だった場合
                    wname = "未指定";

                    CommentWithWnameDto dto = new CommentWithWnameDto();

                    list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
                } else if (!ws.existsWork(wid)) { // コメントの対象作業が削除済みだった場合
                    wname = "削除済み";

                    CommentWithWnameDto dto = new CommentWithWnameDto();

                    list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
                }
            }
        } else {
            for (Comment comment : cr.findByUidAndWidAndCreatedAtBetweenOrderByCreatedAtDesc(uid, wid, since, until)) {
                // String wid = comment.getWid();

                wname = ws.getWork(wid).getWname(); // 作業名を所得

                CommentWithWnameDto dto = new CommentWithWnameDto();

                list.add(dto.build(comment, wname)); // リストに作業名付きコメントを追加
            }
        }

        return list;
    }

    /**
     * 成果を期間指定して所得
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return List<AchivementDto>
     */
    public List<AchivementDto> getAchivement(String uid, String sincest, String untilst) {
        ArrayList<AchivementDto> list = new ArrayList<AchivementDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        for (Comment comment : cr.findByUidAndAchievementflagAndCreatedAtBetweenOrderByCreatedAtAsc(uid, true, since,
                until)) {

            AchivementDto dto = new AchivementDto();

            list.add(dto.build(comment)); // リストに成果を追加
        }

        return list;
    }

    /**
     * ミーティングログを作成
     *
     * @param achivements
     * @return LinkedHashMap<String, List<AchivementDto>>
     */
    public LinkedHashMap<String, List<AchivementDto>> getMeetingLog(List<AchivementDto> achivements) {
        LinkedHashMap<String, List<AchivementDto>> meetinglogs = new LinkedHashMap<String, List<AchivementDto>>();

        ArrayList<String> dates = new ArrayList<String>();

        String date;
        String datecomp = "";

        // 日付のリストを作成
        for (AchivementDto achivement : achivements) {
            date = achivement.getCreatedAtdate();

            if (!date.equals(datecomp)) { // 日時が被っていたら
                dates.add(date);

                datecomp = date;
            }
        }

        // ミーティングログを作成
        for (String dateacv : dates) {
            ArrayList<AchivementDto> list = new ArrayList<AchivementDto>();

            for (AchivementDto achivement : achivements) {
                if (achivement.getCreatedAtdate().equals(dateacv)) { // 該当する日付を持つキーの場所に成果のリストを登録
                    list.add(achivement);
                }
            }
            meetinglogs.put(dateacv, list);
        }

        return meetinglogs;
    }

    /**
     * ミーティングログのotherの部分を作成
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return List<CommentWithWnameDto>
     */
    public List<CommentWithWnameDto> getOthers(String uid, String sincest, String untilst) {
        ArrayList<CommentWithWnameDto> list = new ArrayList<CommentWithWnameDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        for (Comment comment : cr.findByUidAndDiscloseflagAndAchievementflagAndCreatedAtBetweenOrderByCreatedAtAsc(uid,
                true, false, since, until)) {
            // String wid = comment.getWid();

            // wname = ws.getWork(wid).getWname(); // 作業名を所得

            CommentWithWnameDto dto = new CommentWithWnameDto();

            list.add(dto.build(comment, null)); // リストに作業名付きコメントを追加
        }

        return list;
    }

    /* -------------------- Delete -------------------- */

    /**
     * コメントを削除
     *
     * @param uid
     * @param cid
     */
    @Transactional
    public void deleteComment(String uid, Long cid) {
        Comment comment = cr.findById(cid).orElseThrow(() -> new CommentValidationException(COMMENT_DOES_NOT_EXIST,
                "delete the comment", String.format("comment %s does not exist", cid))); // コメントを所得、存在しなければ例外を投げる

        String commentuid = comment.getUid();

        if (!uid.equals(commentuid)) { // 消そうとしているコメントのユーザIDが消そうとしているユーザのものと異なる場合(REST用)
            throw new CommentValidationException(UID_DOSE_NOT_MATCH, "delete the comment",
                    String.format("comment uid %s dose not match deleting uid %s", commentuid, uid)); // 例外を投げる
        }

        cr.deleteById(cid); // コメントを削除
    }

    /**
     * ユーザに紐づくすべてのコメントを削除
     *
     * @param uid
     */
    @Transactional
    public void deleteAllCommnetByUid(String uid) {
        cr.deleteByUid(uid);
    }
}
